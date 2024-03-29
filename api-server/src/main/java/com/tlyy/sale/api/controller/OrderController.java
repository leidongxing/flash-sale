package com.tlyy.sale.api.controller;

import com.tlyy.sale.api.service.order.OrderV1Service;
import com.tlyy.sale.api.service.order.OrderMultiService;
import com.tlyy.sale.api.service.order.PreService;
import com.tlyy.sale.api.vo.*;
import com.tlyy.sale.api.exception.CommonResponse;
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
    private final PreService preService;
    private final OrderV1Service orderV1Service;
//    private final OrderV2Service orderV2Service;
    private final OrderMultiService orderMultiService;

    /**
     * 创建订单 V1
     */
    @PostMapping("/order/v1")
    public CommonResponse createOrderV1(@Validated @RequestBody CreateOrderVO vo) {
        Long id = orderV1Service.createOrderV1(vo.getUid(), vo.getItemId(), vo.getAmount());
        return CommonResponse.success(id);
    }

    /**
     * 创建订单 V2 增加接口限流
     */
    @PostMapping("/order/v2")
    public CommonResponse createOrderV2(@Validated @RequestBody CreateOrderVO vo) {
        preService.rateLimit();
        Long id = orderV1Service.createOrderV1(vo.getUid(), vo.getItemId(), vo.getAmount());
        return CommonResponse.success(id);
    }

    /**
     * 创建秒杀令牌
     */
    @GetMapping("/token")
    public CommonResponse createVerifyKey(@RequestParam("uid") Long uid, @RequestParam("itemId") Long itemId) {
        preService.rateLimit();
        String code = preService.createVerifyKey(uid, itemId);
        return CommonResponse.success(code);
    }

    /**
     * 创建订单 V3  增加秒杀令牌
     */
    @PostMapping("/order/v3")
    public CommonResponse createOrderV3(@Validated @RequestBody CreateOrderVO vo) {
        preService.rateLimit();
        preService.validateToke(vo.getUid(), vo.getItemId(), vo.getToken());
        Long id = orderV1Service.createOrderV1(vo.getUid(), vo.getItemId(), vo.getAmount());
        return CommonResponse.success(id);
    }

    /**
     * 创建订单 V4  增加单用户限制频率 防刷验证
     */
    @PostMapping("/order/v4")
    public CommonResponse createOrderV4(@Validated @RequestBody CreateOrderVO vo) {
        preService.rateLimit();
        preService.validateToke(vo.getUid(), vo.getItemId(), vo.getToken());
        preService.checkUser(vo.getUid());
        Long id = orderV1Service.createOrderV1(vo.getUid(), vo.getItemId(), vo.getAmount());
        return CommonResponse.success(id);
    }

    /**
     * 创建订单 V5  缓存库存 先更新数据库 再删除缓存
     **/
    @PostMapping("/order/v5")
    public CommonResponse createOrderV5(@Validated @RequestBody CreateOrderVO vo) {
//        preService.rateLimit();
//        preService.validateToke(vo.getUid(), vo.getItemId(), vo.getToken());
//        preService.checkUser(vo.getUid());
        Long id = orderV1Service.createOrderV2(vo.getUid(), vo.getItemId(), vo.getAmount());
        return CommonResponse.success(id);
    }

    /**
     * 创建订单 V6  单个队列扣减库存+wait等待
     **/
//    @PostMapping("/order/v6")
//    public CommonResponse createOrderV6(@Validated @RequestBody CreateOrderV1VO vo) {
//        preService.rateLimit();
//        preService.validateToke(vo.getUid(), vo.getItemId(), vo.getToken());
//        preService.checkUser(vo.getUid());
//        Long id = orderV2Service.createOrderV1(vo);
//        return CommonResponse.success(id);
//    }

    /**
     * 创建订单 V7  单个队列扣减库存+condition处理
     **/
//    @PostMapping("/order/v7")
//    public CommonResponse createOrderV7(@Validated @RequestBody CreateOrderV2VO vo) {
//        preService.rateLimit();
//        preService.validateToke(vo.getUid(), vo.getItemId(), vo.getToken());
//        preService.checkUser(vo.getUid());
//        Long id = orderV2Service.createOrderV2(vo);
//        return CommonResponse.success(id);
//    }

    /**
     * 创建订单 V8  多个队列扣减库存++wait等待
     **/
    @PostMapping("/order/v8")
    public CommonResponse createOrderV8(@Validated @RequestBody CreateOrderMultiVO vo) {
//        preService.rateLimit();
//        preService.validateToke(vo.getUid(), vo.getItemId(), vo.getToken());
//        preService.checkUser(vo.getUid());
        vo.setCreateTime(System.currentTimeMillis());
        Long id = orderMultiService.createOrderV1(vo);
        return CommonResponse.success(id);
    }

    /**
     * 创建订单 V9 多个队列扣减库存+condition处理
     **/
    @PostMapping("/order/v9")
    public CommonResponse createOrderV9(@Validated @RequestBody CreateOrderMultiVO vo) {
//        preService.rateLimit();
//        preService.validateToke(vo.getUid(), vo.getItemId(), vo.getToken());
//        preService.checkUser(vo.getUid());
        vo.setCreateTime(System.currentTimeMillis());
        Long id = orderMultiService.createOrderV2(vo);
        return CommonResponse.success(id);
    }

}
