package com.tlyy.sale.api.service.cache;

import cn.hutool.json.JSONUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.tlyy.sale.api.vo.CreateOrderV2VO;
import com.tlyy.sale.api.vo.CreateOrderV3VO;
import com.tlyy.sale.api.entity.ItemStock;
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

    private static final long REDIS_PROCESS_ALLOW_MILLISECOND = 1000L;


    @PostConstruct
    @SuppressWarnings("InfiniteLoopStatement")
    public void initRedisThreadPool() {
        redisMainPool.submit((Runnable) () -> {
            while (true) {
//                CreateOrderV2VO vo = RedisQueue.takeV2();
                CreateOrderV3VO vo = RedisQueue.takeV3();
                redisExecutePool.submit(() -> {
//                    decreaseStockWithLuaV2(vo);
                    decreaseStockWithLuaV3(vo);
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

    private void decreaseStockWithLuaV2(CreateOrderV2VO vo) {
        try {
            vo.setRedisProcessStatus(CreateOrderV2VO.PROCESS_PENDING);
            long current = System.currentTimeMillis();
            if (current - vo.getEnterQueueTime() > REDIS_PROCESS_ALLOW_MILLISECOND) {
                vo.setRedisProcessStatus(CreateOrderV2VO.PROCESS_FAIL);
                log.error("出队时已经超时无需操作,current:{},vo:{}", current, JSONUtil.toJsonStr(vo));
            } else {
                log.debug("redis lua脚本扣减执行");
                DefaultRedisScript<Boolean> defaultRedisScript = new DefaultRedisScript<>(SUB_ITEM_STOCK_LUA_SCRIPT, Boolean.class);
                Boolean result = stringRedisTemplate.execute(defaultRedisScript, Collections.singletonList(generateKey(vo.getItemId())), String.valueOf(vo.getAmount()));
                vo.setRedisProcessStatus(Boolean.TRUE.equals(result) ? CreateOrderV2VO.PROCESS_SUCCESS_DEAL : CreateOrderV2VO.PROCESS_SUCCESS_SOLD_OUT);
            }
        } catch (Exception e) {
            e.printStackTrace();
            vo.setRedisProcessStatus(CreateOrderV2VO.PROCESS_FAIL);
        }
    }

    private void decreaseStockWithLuaV3(CreateOrderV3VO vo) {
        try {
            vo.setRedisProcessStatus(CreateOrderV2VO.PROCESS_PENDING);
            long current = System.currentTimeMillis();
            if (current - vo.getEnterQueueTime() > REDIS_PROCESS_ALLOW_MILLISECOND) {
                vo.setRedisProcessStatus(CreateOrderV2VO.PROCESS_FAIL);
                log.error("出队时已经超时无需操作,current:{},vo:{}", current, JSONUtil.toJsonStr(vo));
            } else {
                log.debug("redis lua脚本扣减执行");
                DefaultRedisScript<Boolean> defaultRedisScript = new DefaultRedisScript<>(SUB_ITEM_STOCK_LUA_SCRIPT, Boolean.class);
                Boolean result = stringRedisTemplate.execute(defaultRedisScript, Collections.singletonList(generateKey(vo.getItemId())), String.valueOf(vo.getAmount()));
                vo.setRedisProcessStatus(Boolean.TRUE.equals(result) ? CreateOrderV2VO.PROCESS_SUCCESS_DEAL : CreateOrderV2VO.PROCESS_SUCCESS_SOLD_OUT);
            }
            vo.getLock().lock();
            vo.getCondition().signal();
            vo.getLock().unlock();
        } catch (Exception e) {
            e.printStackTrace();
            vo.setRedisProcessStatus(CreateOrderV2VO.PROCESS_FAIL);
        }
    }


    private String generateKey(Long itemId) {
        return "stock:itemid:" + itemId;
    }
}
