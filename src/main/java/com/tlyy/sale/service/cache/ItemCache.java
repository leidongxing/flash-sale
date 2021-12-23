package com.tlyy.sale.service.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.tlyy.sale.entity.Item;
import com.tlyy.sale.entity.ItemStock;
import com.tlyy.sale.exception.CommonException;
import com.tlyy.sale.exception.CommonResponseCode;
import lombok.extern.slf4j.Slf4j;

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
public class ItemCache {
    private static Cache<Long, Item> itemCache;
    private static Cache<Long, Long> ItemStockCache;
    private static final ThreadPoolExecutor cacheThreadPool = new ThreadPoolExecutor(2, 10, 1000L, TimeUnit.MICROSECONDS,
            new LinkedBlockingQueue<>(1));

    public static void initItem(List<Item> itemList) {
        itemCache = CacheBuilder.newBuilder()
                .maximumSize(itemList.size())
                .build();
        for (Item item : itemList) {
            itemCache.put(item.getId(), item);
        }
        log.info("init item cache");
    }

    public static void initItemStock(List<ItemStock> itemStockList) {
        ItemStockCache = CacheBuilder.newBuilder()
                .maximumSize(itemStockList.size())
                .build();
        for (ItemStock itemStock : itemStockList) {
            ItemStockCache.put(itemStock.getItemId(), itemStock.getStock());
        }
        log.info("init item stock cache");
    }


    public Item getItemById(Long id) {
        Item item = itemCache.getIfPresent(id);
        if (Objects.isNull(item)) {
            throw new CommonException(CommonResponseCode.ERROR, "错误itemId");
        }
        return item;
    }
}
