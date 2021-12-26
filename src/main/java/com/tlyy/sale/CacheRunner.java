package com.tlyy.sale;

import com.tlyy.sale.entity.Item;
import com.tlyy.sale.entity.ItemStock;
import com.tlyy.sale.mapper.ItemMapper;
import com.tlyy.sale.mapper.ItemStockMapper;
import com.tlyy.sale.service.cache.LocalCache;
import com.tlyy.sale.service.cache.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author LeiDongxing
 * created on 2021/12/12
 * 缓存预热
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class CacheRunner implements ApplicationRunner {
    private final ItemMapper itemMapper;
    private final ItemStockMapper itemStockMapper;
    private final RedisService redisService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<Item> itemList = itemMapper.selectAll();
        LocalCache.initItem(itemList);
        List<ItemStock> itemStockList = itemStockMapper.selectAll();
        LocalCache.initItemStock(itemStockList);
        redisService.initRedisItemStock(itemStockList);
    }
}
