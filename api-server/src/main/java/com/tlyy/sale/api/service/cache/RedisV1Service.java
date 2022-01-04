package com.tlyy.sale.api.service.cache;

import cn.hutool.json.JSONUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.tlyy.sale.api.vo.CreateOrderV1VO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author LeiDongxing
 * created on 2021/12/22
 */
@Slf4j
//@Service
public class RedisV1Service extends RedisCommonService {
    private final StringRedisTemplate stringRedisTemplate;

    private static final ThreadPoolExecutor redisMainPool = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MICROSECONDS,
            new LinkedBlockingQueue<>(1), new ThreadFactoryBuilder().setNameFormat("redis-main").build());

    private static final ThreadPoolExecutor redisExecutePool = new ThreadPoolExecutor(20, 20, 0L, TimeUnit.MICROSECONDS,
            new LinkedBlockingQueue<>(1024), new ThreadFactoryBuilder().setNameFormat("redis-execute-%d").build());

    public RedisV1Service(StringRedisTemplate stringRedisTemplate) {
        super(stringRedisTemplate);
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @PostConstruct
    @SuppressWarnings("InfiniteLoopStatement")
    public void initRedisThreadPool() {
        redisMainPool.submit((Runnable) () -> {
            while (true) {
                CreateOrderV1VO vo = RedisV1Queue.takeV1();
                redisExecutePool.submit(() -> {
                    decreaseStockWithLuaV1(vo);
                });
            }
        });
    }

    private void decreaseStockWithLuaV1(CreateOrderV1VO vo) {
        try {
            vo.setRedisProcessStatus(CreateOrderV1VO.PROCESS_PENDING);
            long current = System.currentTimeMillis();
            if (current - vo.getEnterQueueTime() > REDIS_PROCESS_ALLOW_MILLISECOND) {
                vo.setRedisProcessStatus(CreateOrderV1VO.PROCESS_FAIL);
                log.error("出队时已经超时无需操作,current:{},vo:{}", current, JSONUtil.toJsonStr(vo));
            } else {
                log.debug("redis lua脚本扣减执行");
                DefaultRedisScript<Boolean> defaultRedisScript = new DefaultRedisScript<>(SUB_ITEM_STOCK_LUA_SCRIPT, Boolean.class);
                Boolean result = stringRedisTemplate.execute(defaultRedisScript, Collections.singletonList(generateKey(vo.getItemId())), String.valueOf(vo.getAmount()));
                vo.setRedisProcessStatus(Boolean.TRUE.equals(result) ? CreateOrderV1VO.PROCESS_SUCCESS_DEAL : CreateOrderV1VO.PROCESS_SUCCESS_SOLD_OUT);
            }
        } catch (Exception e) {
            e.printStackTrace();
            vo.setRedisProcessStatus(CreateOrderV1VO.PROCESS_FAIL);
        }
    }
}
