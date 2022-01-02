package com.tlyy.sale;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.tlyy.sale.entity.Item;
import com.tlyy.sale.entity.ItemStock;
import com.tlyy.sale.mapper.ItemMapper;
import com.tlyy.sale.mapper.ItemStockMapper;
import com.tlyy.sale.service.cache.LocalCache;
import com.tlyy.sale.service.cache.RedisService;
import javafx.fxml.LoadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
    //定时任务同步
    private static final ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1,
            new ThreadFactoryBuilder().setNameFormat("cache-reload").build());

    @Override
    public void run(ApplicationArguments args) {
        List<Item> itemList = itemMapper.selectAll();
        LocalCache.initItem(itemList);
        List<ItemStock> itemStockList = itemStockMapper.selectAll();
        LocalCache.initItemStock(itemStockList);
        redisService.initRedisItemStock(itemStockList);
    }


    private void init() {
        log.info("启动更新库存本地缓存任务");
        scheduledThreadPool.scheduleAtFixedRate(() -> {
            List<ItemStock> itemStockList = itemStockMapper.selectAll();
            LocalCache.initItemStock(itemStockList);
        }, 1, 1, TimeUnit.SECONDS);
    }
}
