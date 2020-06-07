package com.tlyy.sale.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.KryoCodec;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author LeiDongxing
 * created on 2020/5/13
 */
@Data
@Configuration
public class RedissonConfig {
    @Bean
    public RedissonClient redissonClient(RedisProperties redisProperties) throws IOException {
        Config config = new Config();
        config.setThreads(5);
        config.setNettyThreads(5);
        config.setCodec(new KryoCodec());
        config.setReferenceEnabled(false);
        config.setLockWatchdogTimeout(40000);
        config.setKeepPubSubOrder(false);
        if (Objects.isNull(redisProperties.getCluster()) && Objects.isNull(redisProperties.getSentinel())) {
            SingleServerConfig singleServerConfig = config.useSingleServer();
            singleServerConfig.setDatabase(redisProperties.getDatabase());
            singleServerConfig.setConnectionMinimumIdleSize(3);
            singleServerConfig.setIdleConnectionTimeout(10000);
            singleServerConfig.setAddress(String.format("redis://%s:%s", redisProperties.getHost(), redisProperties.getPort()));
            singleServerConfig.setPassword(redisProperties.getPassword());
        } else if (Objects.nonNull(redisProperties.getCluster())) {
            ClusterServersConfig clusterServersConfig = config.useClusterServers();
            clusterServersConfig.setPassword(redisProperties.getPassword());
            clusterServersConfig.setIdleConnectionTimeout(10000);
            clusterServersConfig.setScanInterval(2000);
            List<String> clusterNodes = new ArrayList<>();
            for (int i = 0; i < redisProperties.getCluster().getNodes().size(); i++) {
                clusterNodes.add("redis://" + redisProperties.getCluster().getNodes().get(i));
            }
            clusterServersConfig.addNodeAddress(clusterNodes.toString());
        }
        return Redisson.create(config);
    }

}
