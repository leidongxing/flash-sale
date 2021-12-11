package com.tlyy.sale.service.order;

import com.tlyy.sale.entity.Item;
import com.tlyy.sale.entity.ItemOrder;
import com.tlyy.sale.exception.CommonException;
import com.tlyy.sale.exception.CommonResponseCode;
import com.tlyy.sale.mapper.ItemMapper;
import com.tlyy.sale.mapper.ItemOrderMapper;
import com.tlyy.sale.mapper.ItemStockMapper;
import com.tlyy.sale.util.SnowflakeByHandle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author LeiDongxing
 * created on 2020/3/10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final ItemMapper itemMapper;
    private final ItemStockMapper itemStockMapper;
    private final ItemOrderMapper itemOrderMapper;
    private final static SnowflakeByHandle idWorker = new SnowflakeByHandle(0, 0);
    private final StringRedisTemplate stringRedisTemplate;
    public static final String KEY_ITEM_STOCK = "_item_stock_";

    /**
     * 基于数据库行锁保证扣减库存与创建订单一致性
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createOrderV1(Long userId, Long itemId, Long amount) throws CommonException {
        //1.校验下单状态,下单的商品是否存在，用户是否合法，购买数量是否正确
        Item item = itemMapper.selectById(itemId);
        if (item == null) {
            throw new CommonException(CommonResponseCode.ERROR, "商品信息不存在");
        }

        if (amount <= 0 || amount > 1000) {
            throw new CommonException(CommonResponseCode.ERROR, "数量信息不正确");
        }

        //2.落单减库存
        int stockResult = itemStockMapper.decreaseStock(itemId, amount);
        if (stockResult <= 0) {
            throw new CommonException(CommonResponseCode.ERROR, "库存不足");
        }

        //3.订单入库
        ItemOrder order = new ItemOrder();
        order.setUserId(userId);
        order.setItemId(itemId);
        order.setAmount(amount);
        order.setItemPrice(item.getPrice());
        order.setOrderPrice(order.getItemPrice().multiply(new BigDecimal(amount)));
        order.setGmtCreate(new Date());
        order.setGmtModified(new Date());
        order.setDeleted(false);
        //生成交易流水号,订单号
        order.setId(idWorker.nextId());
        int orderResult = itemOrderMapper.insert(order);
        if (orderResult <= 0) {
            throw new CommonException(CommonResponseCode.ERROR, "生成订单失败");
        }

        //加上商品的销量
        int saleReuslt = itemMapper.increaseSales(itemId, amount);
        if (saleReuslt <= 0) {
            throw new CommonException(CommonResponseCode.ERROR, "商品销量失败");
        }
        //4.返回前端
        return order.getId();
    }


    @Transactional(rollbackFor = Exception.class)
    public Long createOrderV2(Long userId, Long itemId, Long amount, String token) throws CommonException {
        //1.校验下单状态,下单的商品是否存在，用户是否合法，购买数量是否正确
        Item item = itemMapper.selectById(itemId);
        if (item == null) {
            throw new CommonException(CommonResponseCode.ERROR, "商品信息不存在");
        }

        if (amount <= 0 || amount > 1000) {
            throw new CommonException(CommonResponseCode.ERROR, "数量信息不正确");
        }

        //2.落单减库存
        Long stockResult = getStockCountByCache(itemId);
        if (stockResult == null) {
            stockResult = getStockCountByCache(itemId);
        }

        if (stockResult <= amount) {
            // 删除库存缓存
            delStockCountCache(itemId);

            //3.订单入库
            ItemOrder order = new ItemOrder();
            order.setUserId(userId);
            order.setItemId(itemId);
            order.setAmount(amount);
            order.setItemPrice(item.getPrice());
            order.setOrderPrice(order.getItemPrice().multiply(new BigDecimal(amount)));
            order.setGmtCreate(new Date());
            order.setGmtModified(new Date());
            order.setDeleted(false);
            //生成交易流水号,订单号
            order.setId(idWorker.nextId());
            int orderResult = itemOrderMapper.insert(order);
            if (orderResult <= 0) {
                throw new CommonException(CommonResponseCode.ERROR, "生成订单失败");
            }

            //加上商品的销量
            int saleReuslt = itemMapper.increaseSales(itemId, amount);
            if (saleReuslt <= 0) {
                throw new CommonException(CommonResponseCode.ERROR, "商品销量失败");
            }
            //4.返回前端
            return order.getId();

        } else {
            throw new CommonException(CommonResponseCode.ERROR, "库存不足");
        }
    }

    public void delStockCountCache(Long itemId) {
        String key = KEY_ITEM_STOCK + itemId;
        stringRedisTemplate.delete(key);
        log.info("删除商品id：[{}] 缓存", itemId);
    }

    public Long getStockCountByCache(Long itemId) {
        String key = KEY_ITEM_STOCK + itemId;
        String countStr = stringRedisTemplate.opsForValue().get(key);
        if (countStr != null) {
            return Long.parseLong(countStr);
        } else {
            return null;
        }
    }

    public Long getStockByDB(Long itemId) {
        return itemStockMapper.selectByItemId(itemId).getStock();
    }
}