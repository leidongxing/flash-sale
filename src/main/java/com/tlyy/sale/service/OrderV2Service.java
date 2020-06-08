package com.tlyy.sale.service;

import cn.hutool.crypto.digest.DigestUtil;
import com.tlyy.sale.entity.Item;
import com.tlyy.sale.exception.CommonException;
import com.tlyy.sale.exception.CommonResponseCode;
import com.tlyy.sale.mapper.ItemMapper;
import com.tlyy.sale.util.SnowflakeByHandle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.tlyy.sale.constant.Constants.HASH_KEY_CREATE_ORDER_V1;

/**
 * @author LeiDongxing
 * create on 2020/6/7 22:08
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderV2Service {
    private final ItemMapper itemMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final OrderV1Service orderV1Service;

    public String createVerifyKey(Long userId, Long itemId) {
        log.info("验证是否在抢购时间内");
        log.info("验证用户是否为合法用户");
        Item item = itemMapper.selectById(itemId);
        if (item == null) {
            throw new CommonException(CommonResponseCode.ERROR, "商品信息不存在");
        }
        String key = userId + HASH_KEY_CREATE_ORDER_V1 + itemId;
        String value = DigestUtil.md5Hex(key);
        //缓存一定时间 过期删除
        stringRedisTemplate.opsForValue().set(key, value, 3600, TimeUnit.SECONDS);
        return value;
    }

    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(Long userId, Long itemId, Long amount, String token) throws CommonException {
        //校验秒杀令牌
        String key = userId + HASH_KEY_CREATE_ORDER_V1 + itemId;
        String value = stringRedisTemplate.opsForValue().get(key);
        if (!(Objects.nonNull(value) && value.equals(token))) {
            throw new CommonException(CommonResponseCode.ERROR, "验证码无效");
        }
        Long orderId = orderV1Service.createOrder(userId, itemId, amount);
        //删除秒杀令牌
        stringRedisTemplate.delete(key);
        return orderId;
    }
}
