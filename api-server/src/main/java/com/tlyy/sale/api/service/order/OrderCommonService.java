package com.tlyy.sale.api.service.order;

import com.tlyy.sale.api.entity.Item;
import com.tlyy.sale.api.entity.ItemOrder;
import com.tlyy.sale.api.exception.CommonException;
import com.tlyy.sale.api.exception.CommonResponseCode;
import com.tlyy.sale.api.mapper.ItemOrderMapper;
import com.tlyy.sale.api.util.SnowflakeByHandle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author LeiDongxing
 * created on 2021/12/23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderCommonService {
    private final ItemOrderMapper itemOrderMapper;
    private final static SnowflakeByHandle idWorker = new SnowflakeByHandle(0, 0);

    public ItemOrder createOrder(Long userId, Item item, Long amount) {
        ItemOrder order = new ItemOrder();
        order.setUserId(userId);
        order.setItemId(item.getId());
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
        return order;
    }
}
