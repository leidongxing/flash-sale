package com.tlyy.sale.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author LeiDongxing
 * created on 2020/3/13
 */
@Service
@RequiredArgsConstructor
public class V2Service {
//    private final StockMapper stockMapper;
//    private final OrderMapper orderMapper;
//
//    public Long createOrderV1(Long stockId) {
//        Stock stock = checkStock(stockId);
//        //扣库存
//        saleStock(stock);
//        //创建订单
//        Order order =new Order();
//        order.setStockId(stock.getId());
//        order.setName(stock.getName());
//        order.setCreateTime(System.currentTimeMillis());
//        orderMapper.insert(order);
//        return order.getId();
//    }
//
//    private Stock checkStock(Long stockId) {
//        Stock stock = stockMapper.selectById(stockId);
//        if (stock.getSale().equals(stock.getCount())) {
//            throw new RuntimeException("库存不足");
//        }
//        return stock;
//    }
//
//    private void saleStock(Stock stock) {
//        stock.setSale(stock.getSale() + 1);
//        stockMapper.updateStockSaleById(stock.getId(), stock.getSale());
//    }
//
//    private void createOrder(Stock stock) {
//        Order order = new Order();
//        order.setStockId(stock.getId());
//        order.setName(stock.getName());
//        orderMapper.insert(order);
//    }
}
