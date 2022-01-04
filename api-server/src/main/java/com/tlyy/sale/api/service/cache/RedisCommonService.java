package com.tlyy.sale.api.service.cache;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.tlyy.sale.api.entity.ItemStock;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author LeiDongxing
 * created on 2022/1/4
 */
@Slf4j
@Service
@AllArgsConstructor
public class RedisCommonService {
    private final StringRedisTemplate stringRedisTemplate;

    protected static final String SUB_ITEM_STOCK_LUA_SCRIPT = "local key=KEYS[1];local num = tonumber(ARGV[1]);local stock = tonumber(redis.call('get',key));" +
            "if (stock<=0) then return false " +
            "elseif (num > stock) then return false " +
            "else redis.call('decrby', KEYS[1], num) return true end";

    protected static final long REDIS_PROCESS_ALLOW_MILLISECOND = 1000L;

    protected String generateKey(Long itemId) {
        return "stock:itemid:" + itemId;
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
}
