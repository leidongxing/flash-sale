package com.tlyy.sale.api.service.redis;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.tlyy.sale.api.service.cache.RedisMultiQueue;
import com.tlyy.sale.api.vo.CreateOrderMultiVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author LeiDongxing
 * created on 2022/1/4
 */
@Slf4j
@Service
public class RedisMultiService extends RedisService {
    private final StringRedisTemplate stringRedisTemplate;

    public RedisMultiService(StringRedisTemplate stringRedisTemplate) {
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
//        for (int i = 0; i < threadPoolArray.length; i++) {
//            int index = i;
//            threadPoolArray[index].submit((Runnable) () -> {
//                while (true) {
//                    CreateOrderMultiVO vo = RedisMultiQueue.takeV1(index);
//                    redisExecutePool.submit(() -> {
//                        decreaseStockWithLuaV1(vo);
//                    });
//                }
//            });
//        }
        for (int i = 0; i < threadPoolArray.length; i++) {
            int index = i;
            threadPoolArray[index].submit((Runnable) () -> {
                while (true) {
                    CreateOrderMultiVO vo = RedisMultiQueue.takeV2(index);
                    redisExecutePool.submit(() -> {
                        decreaseStockWithLuaV2(vo);
                    });
                }
            });
        }
    }
}
