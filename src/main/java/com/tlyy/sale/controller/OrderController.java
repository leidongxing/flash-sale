package com.tlyy.sale.controller;

import com.tlyy.sale.controller.vo.CreateOrderVO;
import com.tlyy.sale.exception.CommonResponse;
import com.tlyy.sale.service.V1Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LeiDongxing
 * created on 2020/3/10
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderController {
    private final V1Service v1Service;

    @PostMapping("/order/v1")
    public CommonResponse createOrderV1(@Validated @RequestBody CreateOrderVO vo) {
        //从token中获取uid
        v1Service.createOrder(1L, vo.getItemId(), vo.getAmount());
        return CommonResponse.success();
    }
}
