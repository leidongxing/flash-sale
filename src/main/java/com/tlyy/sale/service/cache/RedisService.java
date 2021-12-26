package com.tlyy.sale.service.cache;

import cn.hutool.json.JSONUtil;
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

    private static final ThreadPoolExecutor redisThreadPool = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MICROSECONDS,
            new LinkedBlockingQueue<>(2000));

    private final String SUB_ITEM_STOCK_LUA_SCRIPT = "local key=KEYS[1];local num = tonumber(ARGV[1]);local stock = tonumber(redis.call('get',key));" +
            "if (stock<=0) then return false " +
            "elseif (num > stock) then return false " +
            "else redis.call('decrby', KEYS[1], num) return true end";

    private final long REDIS_PROCESS_ALLOW_MILLISECOND = 100L;


    @PostConstruct
    @SuppressWarnings("InfiniteLoopStatement")
    public void initRedisThreadPool() {
        redisThreadPool.submit((Runnable) () -> {
            while (true) {
                try {
                    CreateOrderV2VO vo = RedisQueue.take();
                    vo.setRedisProcessStatus(PROCESS_PENDING);
                    long current = System.currentTimeMillis();
                    if (current - vo.getEnterQueueTime() > REDIS_PROCESS_ALLOW_MILLISECOND) {
                        vo.setRedisProcessStatus(PROCESS_FAIL);
                        log.info("出队redis扣减队列已经超时无需操作,current:{},vo:{}", current, JSONUtil.toJsonStr(vo));
                    } else {
                        log.info("redis lua脚本扣减执行");
                        DefaultRedisScript<Boolean> defaultRedisScript = new DefaultRedisScript<>(SUB_ITEM_STOCK_LUA_SCRIPT, Boolean.class);
                        Boolean result = stringRedisTemplate.execute(defaultRedisScript, Collections.singletonList(generateKey(vo.getItemId())), String.valueOf(vo.getAmount()));
                        vo.setRedisProcessStatus(Boolean.TRUE.equals(result) ? PROCESS_SUCCESS : PROCESS_FAIL);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        log.info("删除商品id：[{}] 缓存", itemId);
    }

    public Long getStockCountByCache(Long itemId) {
        String countStr = stringRedisTemplate.opsForValue().get(generateKey(itemId));
        if (Objects.nonNull(countStr)) {
            return Long.parseLong(countStr);
        }
        log.info("未找到商品id：[{}] 缓存", itemId);
        return -1L;
    }

    private String generateKey(Long itemId) {
        return "stock:itemid:" + itemId;
    }
}
