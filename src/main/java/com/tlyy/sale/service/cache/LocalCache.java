package com.tlyy.sale.service.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.tlyy.sale.entity.Item;
import com.tlyy.sale.entity.ItemStock;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.*;

/**
 * @author LeiDongxing
 * created on 2021/12/22
 */
@Slf4j
public class LocalCache {
    private static Cache<Long, Item> itemCache;
    private static Cache<Long, Long> itemStockCache;

    //定时任务同步
    private static final ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1,
            new ThreadFactoryBuilder().setNameFormat("cache-reload").build());

    public static void initItem(List<Item> itemList) {
        itemCache = CacheBuilder.newBuilder()
                .maximumSize(itemList.size())
                .build();
        for (Item item : itemList) {
            itemCache.put(item.getId(), item);
        }
        log.info("初始化商品本地缓存");
    }

    public static void initItemStock(List<ItemStock> itemStockList) {
        itemStockCache = CacheBuilder.newBuilder()
                .maximumSize(itemStockList.size())
                .build();
        for (ItemStock itemStock : itemStockList) {
            itemStockCache.put(itemStock.getItemId(), itemStock.getStock());
        }
        log.info("初始化库存本地缓存");
    }


    public static Item getItemById(Long id) {
        return itemCache.getIfPresent(id);
    }

    public static Long getItemStockById(Long id) {
        return itemStockCache.getIfPresent(id);
    }

    public static void initUpdateStockTask(List<ItemStock> itemStockList) {
        log.info("启动更新库存本地缓存任务");
        scheduledThreadPool.scheduleAtFixedRate(() -> initItemStock(itemStockList), 1, 1, TimeUnit.SECONDS);
    }

}
