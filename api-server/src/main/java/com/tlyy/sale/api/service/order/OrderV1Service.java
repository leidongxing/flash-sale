package com.tlyy.sale.api.service.order;

import com.tlyy.sale.api.service.redis.RedisCommonService;
import com.tlyy.sale.api.entity.Item;
import com.tlyy.sale.api.entity.ItemOrder;
import com.tlyy.sale.api.exception.CommonException;
import com.tlyy.sale.api.exception.CommonResponseCode;
import com.tlyy.sale.api.mapper.ItemMapper;
import com.tlyy.sale.api.mapper.ItemStockMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * @author LeiDongxing
 * created on 2020/3/10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderV1Service {
    private final ItemMapper itemMapper;
    private final ItemStockMapper itemStockMapper;
    private final RedisCommonService redisCommonService;
    private final OrderCommonService orderCommonService;

    /**
     * 基于数据库行锁保证扣减库存与创建订单一致性
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createOrderV1(Long userId, Long itemId, Long amount) throws CommonException {
        //1.校验下单状态,下单的商品是否存在
        Item item = itemMapper.selectById(itemId);
        if (Objects.isNull(item)) {
            throw new CommonException(CommonResponseCode.ERROR, "商品信息不存在");
        }

        //2.DB扣减库存
        int stockResult = itemStockMapper.decreaseStockWithVersion(itemId, amount);
        if (stockResult <= 0) {
            throw new CommonException(CommonResponseCode.ERROR, "库存不足");
        }

        //3.订单入库
        ItemOrder order = orderCommonService.createOrder(userId, item, amount);

        //4.处理商品的销量
        int saleResult = itemMapper.increaseSales(itemId, amount);
        if (saleResult <= 0) {
            throw new CommonException(CommonResponseCode.ERROR, "商品销量失败");
        }
        //5.返回前端
        return order.getId();
    }

    /**
     * 查询缓存
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createOrderV2(Long userId, Long itemId, Long amount) throws CommonException {
        //1.校验下单状态,下单的商品是否存在
        Item item = itemMapper.selectById(itemId);
        if (Objects.isNull(item)) {
            throw new CommonException(CommonResponseCode.ERROR, "商品信息不存在");
        }

        //2.查询库存
        Long stock = redisCommonService.getStockCountByCache(itemId);
        if (Objects.isNull(stock)) {
            stock = itemStockMapper.selectByItemId(itemId).getStock();
        }

        if (stock >= amount) {
            //3.DB扣减库存
            int stockResult = itemStockMapper.decreaseStockWithVersion(itemId, amount);
            if (stockResult <= 0) {
                throw new CommonException(CommonResponseCode.ERROR, "库存不足");
            }

            //4.删除库存缓存
            redisCommonService.delStockCountCache(itemId);

            //5.订单入库
            ItemOrder order = orderCommonService.createOrder(userId, item, amount);

            //6.处理商品的销量
            int saleResult = itemMapper.increaseSales(itemId, amount);
            if (saleResult <= 0) {
                throw new CommonException(CommonResponseCode.ERROR, "商品销量失败");
            }
            //7.返回前端
            return order.getId();
        } else {
            throw new CommonException(CommonResponseCode.ERROR, "库存不足");
        }
    }
}