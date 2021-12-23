package com.tlyy.sale;

import com.tlyy.sale.util.SnowflakeByHandle;
import com.tlyy.sale.util.SnowflakeByHutool;
import org.junit.Test;
import org.springframework.util.StopWatch;

import java.util.UUID;
import java.util.stream.IntStream;

/**
 * @author LeiDongxing
 * created on 2020/5/5
 */
public class DistributedIdTest {
    int loopCount = 100000;

    @Test
    public void test() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("uuid");
        IntStream.rangeClosed(1, loopCount).parallel().forEach(__ -> UUID.randomUUID().toString());
        stopWatch.stop();

        SnowflakeByHandle idWorker = new SnowflakeByHandle(0, 0);
        stopWatch.start("SnowflakeByHandle");
        IntStream.rangeClosed(1, loopCount).parallel().forEach(__ -> idWorker.nextId());
        stopWatch.stop();

        SnowflakeByHutool snowflakeByHutool = new SnowflakeByHutool();
        stopWatch.start("SnowflakeByHutool");
        IntStream.rangeClosed(1, loopCount).parallel().forEach(__ -> snowflakeByHutool.snowflakeId());
        stopWatch.stop();

        System.out.println(stopWatch.prettyPrint());
    }


}
