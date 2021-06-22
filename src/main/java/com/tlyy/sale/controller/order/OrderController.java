package com.tlyy.sale.controller.order;

import cn.hutool.json.JSONUtil;
import com.google.common.util.concurrent.RateLimiter;
import com.tlyy.sale.controller.vo.CreateOrderVO;
import com.tlyy.sale.exception.CommonResponse;
import com.tlyy.sale.service.order.OrderV1Service;
import com.tlyy.sale.service.order.OrderV2Service;
import com.tlyy.sale.service.order.OrderV3Service;
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
    private final OrderV3Service orderV3Service;
    private final RateLimiter rateLimiter = RateLimiter.create(RATE_LIMIT_COUNT);


    /**
     * 创建订单 V1
     */
    @PostMapping("/order/v1")
    public CommonResponse createOrderV1(@Validated @RequestBody CreateOrderVO vo) {
        //从token中获取uid
        Long id = orderV1Service.createOrder(1L, vo.getItemId(), vo.getAmount());
        return CommonResponse.success(id);
    }

    /**
     * 创建订单 V2 增加接口限流
     */
    @PostMapping("/order/v2")
    public CommonResponse createOrderV2(@Validated @RequestBody CreateOrderVO vo) {
        if (!rateLimiter.tryAcquire(RATE_LIMIT_TIMES, TimeUnit.MICROSECONDS)) {
            log.warn("rate limit function:{}, params:{}", this.getClass().getName(), JSONUtil.toJsonStr(vo));
            return CommonResponse.fail();
        }
        Long id = orderV1Service.createOrder(2L, vo.getItemId(), vo.getAmount());
        return CommonResponse.success(id);
    }

    /**
     * 创建订单 V3  增加秒杀令牌 防刷验证
     */
    @PostMapping("/order/v3")
    public CommonResponse createOrderV3(@Validated @RequestBody CreateOrderVO vo) {
        if (!rateLimiter.tryAcquire(RATE_LIMIT_TIMES, TimeUnit.MICROSECONDS)) {
            log.warn("create order rate limit userId:{}", 2L);
            return CommonResponse.fail();
        }
        Long id = orderV2Service.createOrder(2L, vo.getItemId(), vo.getAmount(), vo.getToken());
        return CommonResponse.success(id);
    }

    /**
     * 创建秒杀令牌
     */
    @GetMapping("/token")
    public CommonResponse createVerifyKey(@RequestParam("itemId") Long itemId) {
        if (!rateLimiter.tryAcquire(RATE_LIMIT_TIMES, TimeUnit.MICROSECONDS)) {
            log.warn("create order rate limit userId:{}", 2L);
            return CommonResponse.fail();
        }
        String code = orderV2Service.createVerifyKey(2L, itemId);
        return CommonResponse.success(code);
    }

    /**
     * 创建订单 V4  单用户限制频率 防刷验证
     */
    @PostMapping("/order/v4")
    public CommonResponse createOrderV4(@Validated @RequestBody CreateOrderVO vo) {
        if (!rateLimiter.tryAcquire(RATE_LIMIT_TIMES, TimeUnit.MICROSECONDS)) {
            log.warn("create order rate limit userId:{}", 2L);
            return CommonResponse.fail();
        }

        Long id = orderV3Service.createOrder(2L, vo.getItemId(), vo.getAmount(), vo.getToken());
        return CommonResponse.success(id);
    }

    /**
     * 创建订单 V5  缓存库存 先更新数据库 再删除缓存
     **/
    @PostMapping("/order/v5")
    public CommonResponse createOrderV5(@Validated @RequestBody CreateOrderVO vo) {
        if (!rateLimiter.tryAcquire(RATE_LIMIT_TIMES, TimeUnit.MICROSECONDS)) {
            log.warn("create order rate limit userId:{}", 2L);
            return CommonResponse.fail();
        }

        Long id = orderV2Service.createOrder(2L, vo.getItemId(), vo.getAmount(), vo.getToken());
        return CommonResponse.success(id);
    }

}
