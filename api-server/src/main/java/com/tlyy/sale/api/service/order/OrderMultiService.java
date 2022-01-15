package com.tlyy.sale.api.service.order;

import cn.hutool.json.JSONUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.tlyy.sale.api.entity.Item;
import com.tlyy.sale.api.entity.ItemOrder;
import com.tlyy.sale.api.exception.CommonException;
import com.tlyy.sale.api.exception.CommonResponseCode;
import com.tlyy.sale.api.mapper.ItemMapper;
import com.tlyy.sale.api.mapper.ItemStockMapper;
import com.tlyy.sale.api.service.cache.LocalCache;
import com.tlyy.sale.api.service.cache.RedisMultiQueue;
import com.tlyy.sale.api.vo.CreateOrderMultiVO;
import com.tlyy.sale.api.vo.CreateOrderV1VO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author LeiDongxing
 * created on 2022/1/5
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderMultiService {
    private final OrderCommonService orderCommonService;
    private final ItemStockMapper itemStockMapper;
    private final ItemMapper itemMapper;
    private static final ThreadPoolExecutor dbThreadPool = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MICROSECONDS,
            new LinkedBlockingQueue<>(10240), new ThreadFactoryBuilder().setNameFormat("db-thread-%d").build());
    private final static long ALL_PROCESS_ALLOW_MILLISECOND = 1000L;


    public Long createOrderV1(CreateOrderMultiVO vo) throws CommonException {
        Long itemId = vo.getItemId();
        Long amount = vo.getAmount();
        Long userId = vo.getUid();
        long startTime = System.currentTimeMillis();

        //1.校验下单状态,下单的商品是否存在
        Item item = LocalCache.getItemById(itemId);
        if (Objects.isNull(item)) {
            throw new CommonException(CommonResponseCode.ERROR, "商品信息不存在");
        }
        //2.初步内存校验库存
        Long stock = LocalCache.getItemStockById(itemId);
        if (Objects.isNull(stock) || stock <= 0) {
            throw new CommonException(CommonResponseCode.ERROR, "内存库存不足");
        }

        //3.请求进入redis扣减队列
        RedisMultiQueue.offerV1(vo);

        //4.订单入库
        while (System.currentTimeMillis() - startTime < ALL_PROCESS_ALLOW_MILLISECOND) {
            switch (vo.getRedisProcessStatus()) {
                case CreateOrderV1VO.PROCESS_FAIL:
                    log.error("处理失败,vo:{}", JSONUtil.toJsonStr(vo));
                    throw new CommonException(CommonResponseCode.ERROR, "处理失败");
                case CreateOrderV1VO.PROCESS_SUCCESS_SOLD_OUT:
                    throw new CommonException(CommonResponseCode.ERROR, "库存不足");
                case CreateOrderV1VO.PROCESS_SUCCESS_DEAL:
                    //4.订单入库
                    ItemOrder order = orderCommonService.createOrder(userId, item, amount);
                    //5.异步处理 增加商品的销量并减去库存
                    dbThreadPool.submit(() -> {
                        int stockResult = itemStockMapper.decreaseStock(itemId, amount);
                        if (stockResult <= 0) {
                            log.error("异步扣减库存失败");
                        }
                        int saleResult = itemMapper.increaseSales(itemId, amount);
                        if (saleResult <= 0) {
                            log.error("异步增加销量失败");
                        }
                    });
                    //5.返回前端
                    vo.setProcessTime(System.currentTimeMillis());
                    return order.getId();
                case CreateOrderV1VO.UN_PROCESS:
                case CreateOrderV1VO.PROCESS_PENDING:
                    try {
                        TimeUnit.MICROSECONDS.sleep(20);
                    } catch (InterruptedException e) {
                        throw new CommonException(CommonResponseCode.ERROR, "等待redis扣减异常");
                    }
            }
        }
        log.error("处理超时,vo:{}", JSONUtil.toJsonStr(vo));
        throw new CommonException(CommonResponseCode.ERROR, "处理超时");
    }

    public Long createOrderV2(CreateOrderMultiVO vo) throws CommonException {
        Long itemId = vo.getItemId();
        Long amount = vo.getAmount();
        Long userId = vo.getUid();

        //1.校验下单状态,下单的商品是否存在
        Item item = LocalCache.getItemById(itemId);
        if (Objects.isNull(item)) {
            throw new CommonException(CommonResponseCode.ERROR, "商品信息不存在");
        }
        //2.初步内存校验库存
        Long stock = LocalCache.getItemStockById(itemId);
        if (Objects.isNull(stock) || stock <= 0) {
            throw new CommonException(CommonResponseCode.ERROR, "内存库存不足");
        }

        //3.请求进入redis扣减队列
        RedisMultiQueue.offerV2(vo);

        //4.等待请求出队
        switch (vo.getRedisProcessStatus()) {
            case CreateOrderMultiVO.PROCESS_FAIL:
                log.error("处理失败,vo:{}", JSONUtil.toJsonStr(vo));
                throw new CommonException(CommonResponseCode.ERROR, "处理失败");
            case CreateOrderMultiVO.PROCESS_SUCCESS_SOLD_OUT:
                throw new CommonException(CommonResponseCode.ERROR, "库存不足");
            case CreateOrderMultiVO.PROCESS_SUCCESS_DEAL:
                //4.订单入库
                ItemOrder order = orderCommonService.createOrder(userId, item, amount);
                //5.异步处理 增加商品的销量并减去库存
                dbThreadPool.submit(() -> {
                    int stockResult = itemStockMapper.decreaseStock(itemId, amount);
                    if (stockResult <= 0) {
                        log.error("异步扣减库存失败");
                    }
                    int saleResult = itemMapper.increaseSales(itemId, amount);
                    if (saleResult <= 0) {
                        log.error("异步增加销量失败");
                    }
                });
                //5.返回前端
                vo.setProcessTime(System.currentTimeMillis());
                return order.getId();
            case CreateOrderMultiVO.UN_PROCESS:
                throw new CommonException(CommonResponseCode.ERROR, "状态异常-未处理");
            case CreateOrderMultiVO.PROCESS_PENDING:
                throw new CommonException(CommonResponseCode.ERROR, "状态异常-处理中");
            default:
                throw new CommonException(CommonResponseCode.ERROR, "状态异常");
        }
    }
}
