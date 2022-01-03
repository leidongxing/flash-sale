package com.tlyy.sale;

import com.tlyy.sale.api.mapper.ItemMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author LeiDongxing
 * created on 2022/1/1
 */
@Slf4j
@SpringBootTest
public class MySQLTest {
    @Autowired
    private ItemMapper itemMapper;

    @Test
    public void testUpdate() {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 1000; i++) {
            executor.submit(() -> {
                int saleResult = itemMapper.increaseSales(1L, 1L);
                if (saleResult <= 0) {
                    log.error("异步增加销量失败");
                } else {
                    log.info("增加成功 saleResult:{}", saleResult);
                }
            });
        }
        try {
            TimeUnit.MINUTES.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
