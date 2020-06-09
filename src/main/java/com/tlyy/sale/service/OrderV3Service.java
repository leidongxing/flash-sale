package com.tlyy.sale.service;

import com.tlyy.sale.exception.CommonException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

import static com.tlyy.sale.constant.Constants.*;

/**
 * @author LeiDongxing
 * create on 2020/6/9 23:47
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderV3Service {
    private final OrderV2Service orderV2Service;
    private final StringRedisTemplate stringRedisTemplate;

    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(Long userId, Long itemId, Long amount, String token) throws CommonException {
        if (!checkUser(userId)) {
            log.warn("存在风险用户 userId:{}", userId);
        }
        return orderV2Service.createOrder(userId, itemId, amount, token);
    }

    private void addUserCount(Long userId, int times) {
        String limitKey = userId + KEY_LIMIT_USER;
        stringRedisTemplate.opsForValue().set(limitKey, String.valueOf(times), 5, TimeUnit.SECONDS);
    }

    private boolean checkUser(Long userId) {
        String limitKey = userId + KEY_LIMIT_USER;
        String limitNum = stringRedisTemplate.opsForValue().get(limitKey);
        if (limitNum == null) {
            log.info("该用户首次进入");
            addUserCount(userId, 0);
            return true;
        }
        int times = Integer.parseInt(limitNum);
        addUserCount(userId, times + 1);
        return times < TIMES_LIMIT_USER;
    }
}
