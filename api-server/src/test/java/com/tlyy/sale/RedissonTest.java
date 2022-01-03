package com.tlyy.sale;

import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.*;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;

/**
 * @author LeiDongxing
 * created on 2020/5/13
 */
public class RedissonTest {
    @Test
    public void test() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        RedissonClient redisson = Redisson.create(config);
        RedissonReactiveClient redissonReactive = Redisson.createReactive(config);
        RedissonRxClient redissonRx = Redisson.createRx(config);

        RLockReactive lock = redissonReactive.getLock("dasd");
        lock.tryLock();


        RBucket<String> bucket = redisson.getBucket("test");
        bucket.set("456");
        System.out.println(redisson.getBucket("das").get());
//        boolean isUpdated = bucket.compareAndSet("123", "4934");
//        String prevObject = bucket.getAndSet("321");
//        boolean isSet = bucket.trySet("901");
//        long objectSize = bucket.size();
//
//        // set with expiration
//        bucket.set("value", 10, TimeUnit.SECONDS);
//        boolean isNewSet = bucket.trySet("nextValue", 10, TimeUnit.SECONDS);
        redisson.shutdown();
    }
}
