package com.tlyy.sale.controller;

import com.google.common.util.concurrent.RateLimiter;
import com.tlyy.sale.controller.vo.CreateOrderVO;
import com.tlyy.sale.exception.CommonResponse;
import com.tlyy.sale.service.OrderV1Service;
import com.tlyy.sale.service.OrderV2Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

import static com.tlyy.sale.constant.Constants.RATE_LIMIT_COUNT;
import static com.tlyy.sale.constant.Constants.RATE_LIMIT_TIMES;

/**
 * @author LeiDongxing
 * created on 2020/3/10
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderV1Service orderV1Service;
    private final OrderV2Service orderV2Service;
    private RateLimiter rateLimiter = RateLimiter.create(RATE_LIMIT_COUNT);


    @PostMapping("/order/v1")
    public CommonResponse createOrderV1(@Validated @RequestBody CreateOrderVO vo) {
        //从token中获取uid
        Long id = orderV1Service.createOrder(1L, vo.getItemId(), vo.getAmount());
        return CommonResponse.success(id);
    }

    @PostMapping("/order/v2")
    public CommonResponse createOrderV2(@Validated @RequestBody CreateOrderVO vo) {
        //增加接口限流
        if (!rateLimiter.tryAcquire(RATE_LIMIT_TIMES, TimeUnit.MICROSECONDS)) {
            log.warn("create order rate limit userId:{}", 2L);
            return CommonResponse.fail();
        }
        Long id = orderV1Service.createOrder(2L, vo.getItemId(), vo.getAmount());
        return CommonResponse.success(id);
    }


    @PostMapping("/order/v3")
    public CommonResponse createOrderV3(@Validated @RequestBody CreateOrderVO vo) {
        if (!rateLimiter.tryAcquire(RATE_LIMIT_TIMES, TimeUnit.MICROSECONDS)) {
            log.warn("create order rate limit userId:{}", 2L);
            return CommonResponse.fail();
        }
        //增加防刷验证
        Long id = orderV2Service.createOrder(2L, vo.getItemId(), vo.getAmount(), vo.getKey());
        return CommonResponse.success(id);
    }

    @GetMapping("/key")
    public CommonResponse createVerifyKey(@RequestParam("itemId") Long itemId) {
        String code = orderV2Service.createVerifyKey(2L, itemId);
        return CommonResponse.success(code);
    }


}
