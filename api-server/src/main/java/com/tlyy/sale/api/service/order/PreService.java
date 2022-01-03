package com.tlyy.sale.api.service.order;

import cn.hutool.crypto.digest.DigestUtil;
import com.google.common.util.concurrent.RateLimiter;
import com.tlyy.sale.api.entity.Item;
import com.tlyy.sale.api.exception.CommonException;
import com.tlyy.sale.api.exception.CommonResponseCode;
import com.tlyy.sale.api.mapper.ItemMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * @author LeiDongxing
 * created on 2021/12/6
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PreService {
    private final int RATE_LIMIT_COUNT = 100;
    private final int RATE_LIMIT_TIMES = 100;
    public static final String KEY_CREATE_ORDER = "_create_order_";
    public static final String KEY_LIMIT_USER = "_limit_user_";
    public static final int TIMES_LIMIT_USER = 50;
    private final RateLimiter rateLimiter = RateLimiter.create(RATE_LIMIT_COUNT);
    private final StringRedisTemplate stringRedisTemplate;
    private final ItemMapper itemMapper;

    public void rateLimit() {
        if (!rateLimiter.tryAcquire(RATE_LIMIT_TIMES, TimeUnit.MICROSECONDS)) {
            throw new CommonException(CommonResponseCode.ERROR, "rate limit");
        }
    }

    public String createVerifyKey(Long userId, Long itemId) {
        log.info("验证是否在抢购时间内");
        log.info("验证用户是否为合法用户");
        Item item = itemMapper.selectById(itemId);
        if (item == null) {
            throw new CommonException(CommonResponseCode.ERROR, "商品信息不存在");
        }
        String key = userId + KEY_CREATE_ORDER + itemId;
        String value = DigestUtil.md5Hex(key);
        //缓存一定时间 过期删除
        stringRedisTemplate.opsForValue().set(key, value, 3600, TimeUnit.SECONDS);
        return value;
    }


    public void validateToke(Long userId, Long itemId, String token) {
        //校验秒杀令牌
        String key = userId + KEY_CREATE_ORDER + itemId;
        String value = stringRedisTemplate.opsForValue().get(key);
        if (!(Objects.nonNull(value) && value.equals(token))) {
            throw new CommonException(CommonResponseCode.ERROR, "token invalid");
        }
        //删除秒杀令牌
        stringRedisTemplate.delete(key);
    }

    public void checkUser(Long userId) {
        String limitKey = userId + KEY_LIMIT_USER;
        String limitNum = stringRedisTemplate.opsForValue().get(limitKey);
        if (limitNum == null) {
            log.info("该用户首次进入");
            addUserCount(userId, 0);
        }
        int times = Integer.parseInt(limitNum);
        addUserCount(userId, times + 1);
        if (times >= TIMES_LIMIT_USER) {
            throw new CommonException(CommonResponseCode.ERROR, "check user error");
        }
    }

    private void addUserCount(Long userId, int times) {
        String limitKey = userId + KEY_LIMIT_USER;
        stringRedisTemplate.opsForValue().set(limitKey, String.valueOf(times), 5, TimeUnit.SECONDS);
    }

}
