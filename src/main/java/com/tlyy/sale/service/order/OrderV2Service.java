package com.tlyy.sale.service.order;

import cn.hutool.json.JSONUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.tlyy.sale.entity.Item;
import com.tlyy.sale.entity.ItemOrder;
import com.tlyy.sale.exception.CommonException;
import com.tlyy.sale.exception.CommonResponseCode;
import com.tlyy.sale.mapper.ItemMapper;
import com.tlyy.sale.mapper.ItemStockMapper;
import com.tlyy.sale.service.cache.LocalCache;
import com.tlyy.sale.service.cache.RedisQueue;
import com.tlyy.sale.vo.CreateOrderV2VO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.tlyy.sale.vo.CreateOrderV2VO.*;


/**
 * @author LeiDongxing
 * created on 2021/12/12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderV2Service {
    private final ItemStockMapper itemStockMapper;
    private final ItemMapper itemMapper;
    private final OrderCommonService orderCommonService;
    private final static long ALL_PROCESS_ALLOW_MILLISECOND = 200L;

    private static final ThreadPoolExecutor dbThreadPool = new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MICROSECONDS,
            new LinkedBlockingQueue<>(10240), new ThreadFactoryBuilder().setNameFormat("db-thread-%d").build());


    public Long createOrderV1(CreateOrderV2VO vo) throws CommonException {
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
        RedisQueue.offer(vo);

        //4.订单入库
        int waitTimes = 0;
        while (System.currentTimeMillis() - startTime < ALL_PROCESS_ALLOW_MILLISECOND) {
            switch (vo.getRedisProcessStatus()) {
                case PROCESS_FAIL:
                    log.error("处理失败，等待处理次数waitTime:{},vo:{}", waitTimes, JSONUtil.toJsonStr(vo));
                    throw new CommonException(CommonResponseCode.ERROR, "处理失败");
                case PROCESS_SUCCESS_SOLD_OUT:
                    throw new CommonException(CommonResponseCode.ERROR, "库存不足");
                case PROCESS_SUCCESS_DEAL:
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
                        LocalCache.decreaseStock(itemId, amount);
                    });
                    //5.返回前端
                    return order.getId();
                case UN_PROCESS:
                case PROCESS_PENDING:
                    try {
                        waitTimes++;
                        TimeUnit.MICROSECONDS.sleep(20);
                    } catch (InterruptedException e) {
                        throw new CommonException(CommonResponseCode.ERROR, "等待redis扣减异常");
                    }
            }
        }
        log.info("处理超时，等待处理次数waitTime:{},vo:{}", waitTimes, JSONUtil.toJsonStr(vo));
        throw new CommonException(CommonResponseCode.ERROR, "处理超时");
    }
}
