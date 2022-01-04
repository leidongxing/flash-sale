package com.tlyy.sale.api.service.cache;

import cn.hutool.json.JSONUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.tlyy.sale.api.vo.CreateOrderV3VO;
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
 * created on 2022/1/4
 */
@Slf4j
@Service
public class RedisV3Service extends RedisCommonService {
    private final StringRedisTemplate stringRedisTemplate;

    public RedisV3Service(StringRedisTemplate stringRedisTemplate) {
        super(stringRedisTemplate);
        this.stringRedisTemplate = stringRedisTemplate;
    }

    private static final ThreadPoolExecutor redisMainPool0 = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MICROSECONDS,
            new LinkedBlockingQueue<>(1), new ThreadFactoryBuilder().setNameFormat("redis-main-0").build());

    private static final ThreadPoolExecutor redisMainPool1 = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MICROSECONDS,
            new LinkedBlockingQueue<>(1), new ThreadFactoryBuilder().setNameFormat("redis-main-1").build());

    private static final ThreadPoolExecutor redisMainPool2 = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MICROSECONDS,
            new LinkedBlockingQueue<>(1), new ThreadFactoryBuilder().setNameFormat("redis-main-2").build());

    private static final ThreadPoolExecutor redisMainPool3 = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MICROSECONDS,
            new LinkedBlockingQueue<>(1), new ThreadFactoryBuilder().setNameFormat("redis-main-3").build());

    private static final ThreadPoolExecutor redisMainPool4 = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MICROSECONDS,
            new LinkedBlockingQueue<>(1), new ThreadFactoryBuilder().setNameFormat("redis-main-4").build());

    private static final ThreadPoolExecutor redisMainPool5 = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MICROSECONDS,
            new LinkedBlockingQueue<>(1), new ThreadFactoryBuilder().setNameFormat("redis-main-5").build());

    private static final ThreadPoolExecutor redisMainPool6 = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MICROSECONDS,
            new LinkedBlockingQueue<>(1), new ThreadFactoryBuilder().setNameFormat("redis-main-6").build());

    private static final ThreadPoolExecutor redisMainPool7 = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MICROSECONDS,
            new LinkedBlockingQueue<>(1), new ThreadFactoryBuilder().setNameFormat("redis-main-7").build());

    private static final ThreadPoolExecutor redisMainPool8 = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MICROSECONDS,
            new LinkedBlockingQueue<>(1), new ThreadFactoryBuilder().setNameFormat("redis-main-8").build());

    private static final ThreadPoolExecutor redisMainPool9 = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MICROSECONDS,
            new LinkedBlockingQueue<>(1), new ThreadFactoryBuilder().setNameFormat("redis-main-9").build());

    private static final ThreadPoolExecutor redisExecutePool = new ThreadPoolExecutor(20, 20, 0L, TimeUnit.MICROSECONDS,
            new LinkedBlockingQueue<>(1024), new ThreadFactoryBuilder().setNameFormat("redis-execute-%d").build());

    private static final ThreadPoolExecutor[] threadPoolArray = new ThreadPoolExecutor[]{redisMainPool0, redisMainPool1, redisMainPool2, redisMainPool3, redisMainPool4,
            redisMainPool5, redisMainPool6, redisMainPool7, redisMainPool8, redisMainPool9};

    @PostConstruct
    @SuppressWarnings("InfiniteLoopStatement")
    public void initRedisThreadPool() {
        for (int i = 0; i < threadPoolArray.length; i++) {
            int index = i;
            threadPoolArray[index].submit((Runnable) () -> {
                while (true) {
                    CreateOrderV3VO vo = RedisV3Queue.takeV3(index);
                    redisExecutePool.submit(() -> {
                        decreaseStockWithLuaV3(vo);
                    });
                }
            });
        }
    }


    private void decreaseStockWithLuaV3(CreateOrderV3VO vo) {
        try {
            vo.setRedisProcessStatus(CreateOrderV3VO.PROCESS_PENDING);
            long current = System.currentTimeMillis();
            if (current - vo.getEnterQueueTime() > REDIS_PROCESS_ALLOW_MILLISECOND) {
                vo.setRedisProcessStatus(CreateOrderV3VO.PROCESS_FAIL);
                log.error("出队时已经超时无需操作,current:{},vo:{}", current, JSONUtil.toJsonStr(vo));
            } else {
                log.debug("redis lua脚本扣减执行");
                DefaultRedisScript<Boolean> defaultRedisScript = new DefaultRedisScript<>(SUB_ITEM_STOCK_LUA_SCRIPT, Boolean.class);
                Boolean result = this.stringRedisTemplate.execute(defaultRedisScript, Collections.singletonList(generateKey(vo.getItemId())), String.valueOf(vo.getAmount()));
                vo.setRedisProcessStatus(Boolean.TRUE.equals(result) ? CreateOrderV3VO.PROCESS_SUCCESS_DEAL : CreateOrderV3VO.PROCESS_SUCCESS_SOLD_OUT);
            }
            vo.getLock().lock();
            vo.getCondition().signal();
            vo.getLock().unlock();
        } catch (Exception e) {
            e.printStackTrace();
            vo.setRedisProcessStatus(CreateOrderV3VO.PROCESS_FAIL);
        }
    }


}
