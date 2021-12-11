package com.tlyy.sale.controller;

import com.tlyy.sale.exception.CommonResponse;
import com.tlyy.sale.service.order.OrderService;
import com.tlyy.sale.service.order.PreService;
import com.tlyy.sale.vo.CreateOrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * @author LeiDongxing
 * created on 2020/3/10
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final PreService preService;


    /**
     * 创建订单 V1
     */
    @PostMapping("/order/v1")
    public CommonResponse createOrderV1(@Validated @RequestBody CreateOrderVO vo) {
        //从token中获取uid
        Long id = orderService.createOrderV1(1L, vo.getItemId(), vo.getAmount());
        return CommonResponse.success(id);
    }

    /**
     * 创建订单 V2 增加接口限流
     */
    @PostMapping("/order/v2")
    public CommonResponse createOrderV2(@Validated @RequestBody CreateOrderVO vo) {
        preService.rateLimit();
        //从token中获取uid
        Long id = orderService.createOrderV1(2L, vo.getItemId(), vo.getAmount());
        return CommonResponse.success(id);
    }


    /**
     * 创建秒杀令牌
     */
    @GetMapping("/token")
    public CommonResponse createVerifyKey(@RequestParam("itemId") Long itemId) {
        preService.rateLimit();
        String code = preService.createVerifyKey(3L, itemId);
        return CommonResponse.success(code);
    }

    /**
     * 创建订单 V3  增加秒杀令牌
     */
    @PostMapping("/order/v3")
    public CommonResponse createOrderV3(@Validated @RequestBody CreateOrderVO vo) {
        preService.rateLimit();
        preService.validateToke(3L, vo.getItemId(), vo.getToken());
        Long id = orderService.createOrderV1(3L, vo.getItemId(), vo.getAmount());
        return CommonResponse.success(id);
    }


    /**
     * 创建订单 V4  增加单用户限制频率 防刷验证
     */
    @PostMapping("/order/v4")
    public CommonResponse createOrderV4(@Validated @RequestBody CreateOrderVO vo) {
        preService.rateLimit();
        preService.validateToke(4L, vo.getItemId(), vo.getToken());
        preService.checkUser(4L);
        Long id = orderService.createOrderV1(4L, vo.getItemId(), vo.getAmount());
        return CommonResponse.success(id);
    }

    /**
     * 创建订单 V5  缓存库存 先更新数据库 再删除缓存
     **/
    @PostMapping("/order/v5")
    public CommonResponse createOrderV5(@Validated @RequestBody CreateOrderVO vo) {
        preService.rateLimit();
        preService.validateToke(5L, vo.getItemId(), vo.getToken());
        preService.checkUser(5L);
        Long id = orderService.createOrderV2(5L, vo.getItemId(), vo.getAmount(), vo.getToken());
        return CommonResponse.success(id);
    }

}
