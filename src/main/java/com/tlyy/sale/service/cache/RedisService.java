package com.tlyy.sale.service.cache;

import cn.hutool.json.JSONUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.tlyy.sale.entity.ItemStock;
import com.tlyy.sale.vo.CreateOrderV2VO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.tlyy.sale.vo.CreateOrderV2VO.*;

/**
 * @author LeiDongxing
 * created on 2021/12/22
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {
    private final StringRedisTemplate stringRedisTemplate;

    private static final ThreadPoolExecutor redisMainPool = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MICROSECONDS,
            new LinkedBlockingQueue<>(1), new ThreadFactoryBuilder().setNameFormat("redis-main").build());
    private static final ThreadPoolExecutor redisExecutePool = new ThreadPoolExecutor(20, 20, 0L, TimeUnit.MICROSECONDS,
            new LinkedBlockingQueue<>(1024), new ThreadFactoryBuilder().setNameFormat("redis-execute-%d").build());

    private static final String SUB_ITEM_STOCK_LUA_SCRIPT = "local key=KEYS[1];local num = tonumber(ARGV[1]);local stock = tonumber(redis.call('get',key));" +
            "if (stock<=0) then return false " +
            "elseif (num > stock) then return false " +
            "else redis.call('decrby', KEYS[1], num) return true end";

    private static final long REDIS_PROCESS_ALLOW_MILLISECOND = 200L;


    @PostConstruct
    @SuppressWarnings("InfiniteLoopStatement")
    public void initRedisThreadPool() {
        redisMainPool.submit((Runnable) () -> {
            while (true) {
                CreateOrderV2VO vo = RedisQueue.take();
                redisExecutePool.submit(() -> {
                    decreaseStockWithLua(vo);
                });
            }
        });
    }


    public void initRedisItemStock(List<ItemStock> itemStockList) {
        for (ItemStock itemStock : itemStockList) {
            stringRedisTemplate.opsForValue().set(generateKey(itemStock.getItemId()), String.valueOf(itemStock.getStock()));
        }
        log.info("初始化redis库存缓存");
    }


    public void delStockCountCache(Long itemId) {
        stringRedisTemplate.delete(generateKey(itemId));
        log.debug("删除商品id：[{}] 缓存", itemId);
    }

    public Long getStockCountByCache(Long itemId) {
        String countStr = stringRedisTemplate.opsForValue().get(generateKey(itemId));
        if (Objects.nonNull(countStr)) {
            return Long.parseLong(countStr);
        }
        log.debug("未找到商品id：[{}] 缓存", itemId);
        return -1L;
    }

    private void decreaseStockWithLua(CreateOrderV2VO vo) {
        try {
            vo.setRedisProcessStatus(PROCESS_PENDING);
            long current = System.currentTimeMillis();
            if (current - vo.getEnterQueueTime() > REDIS_PROCESS_ALLOW_MILLISECOND) {
                vo.setRedisProcessStatus(PROCESS_FAIL);
                log.error("出队redis扣减队列已经超时无需操作,current:{},vo:{}", current, JSONUtil.toJsonStr(vo));
            } else {
                log.debug("redis lua脚本扣减执行");
                DefaultRedisScript<Boolean> defaultRedisScript = new DefaultRedisScript<>(SUB_ITEM_STOCK_LUA_SCRIPT, Boolean.class);
                Boolean result = stringRedisTemplate.execute(defaultRedisScript, Collections.singletonList(generateKey(vo.getItemId())), String.valueOf(vo.getAmount()));
                vo.setRedisProcessStatus(Boolean.TRUE.equals(result) ? PROCESS_SUCCESS_DEAL : PROCESS_SUCCESS_SOLD_OUT);
            }
        } catch (Exception e) {
            e.printStackTrace();
            vo.setRedisProcessStatus(PROCESS_FAIL);
        }
    }

    private String generateKey(Long itemId) {
        return "stock:itemid:" + itemId;
    }
}