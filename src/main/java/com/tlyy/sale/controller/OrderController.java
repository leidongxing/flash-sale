package com.tlyy.sale.controller;

import com.tlyy.sale.exception.CommonResponse;
import com.tlyy.sale.service.order.OrderV1Service;
import com.tlyy.sale.service.order.OrderV2Service;
import com.tlyy.sale.service.order.PreService;
import com.tlyy.sale.vo.CreateOrderV1VO;
import com.tlyy.sale.vo.CreateOrderV2VO;
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
    private final OrderV2Service orderV2Service;


    /**
     * 创建订单 V1
     */
    @PostMapping("/order/v1")
    public CommonResponse createOrderV1(@Validated @RequestBody CreateOrderV1VO vo) {
        Long id = orderV1Service.createOrderV1(vo.getUid(), vo.getItemId(), vo.getAmount());
        return CommonResponse.success(id);
    }

    /**
     * 创建订单 V2 增加接口限流
     */
    @PostMapping("/order/v2")
    public CommonResponse createOrderV2(@Validated @RequestBody CreateOrderV1VO vo) {
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
    public CommonResponse createOrderV3(@Validated @RequestBody CreateOrderV1VO vo) {
        preService.rateLimit();
        preService.validateToke(vo.getUid(), vo.getItemId(), vo.getToken());
        Long id = orderV1Service.createOrderV1(vo.getUid(), vo.getItemId(), vo.getAmount());
        return CommonResponse.success(id);
    }


    /**
     * 创建订单 V4  增加单用户限制频率 防刷验证
     */
    @PostMapping("/order/v4")
    public CommonResponse createOrderV4(@Validated @RequestBody CreateOrderV1VO vo) {
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
    public CommonResponse createOrderV5(@Validated @RequestBody CreateOrderV1VO vo) {
//        preService.rateLimit();
//        preService.validateToke(vo.getUid(), vo.getItemId(), vo.getToken());
//        preService.checkUser(vo.getUid());
        Long id = orderV1Service.createOrderV2(vo.getUid(), vo.getItemId(), vo.getAmount());
        return CommonResponse.success(id);
    }


    /**
     * 创建订单 V6  队列化扣减库存+wait等待
     **/
    @PostMapping("/order/v6")
    public CommonResponse createOrderV6(@Validated @RequestBody CreateOrderV2VO vo) {
//        preService.rateLimit();
//        preService.validateToke(vo.getUid(), vo.getItemId(), vo.getToken());
//        preService.checkUser(vo.getUid());
        Long id = orderV2Service.createOrderV1(vo);
        return CommonResponse.success(id);
    }
}
