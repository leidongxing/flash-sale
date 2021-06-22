package com.tlyy.sale.service.url;

import com.tlyy.sale.config.TinyUrlConfig;
import com.tlyy.sale.entity.UrlMapping;
import com.tlyy.sale.mapper.UrlMappingMapper;
import com.tlyy.sale.util.NumericConvertUtils;
import com.tlyy.sale.util.SnowflakeByHutool;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author LeiDongxing
 * created on 2021/6/21
 */
@Component
@RequiredArgsConstructor
public class DefaultStrategy implements IStrategy {
    private final RedissonClient redissonClient;

    private final UrlMappingMapper urlMappingMapper;

    private final TinyUrlConfig tinyUrlConfig;

    private final SnowflakeByHutool snowflake;

    @Override
    public String getTinyUrl(String longUrl) {
        RBucket<String> bucket = redissonClient.getBucket(longUrl);
        if (Objects.isNull(bucket.get())) {
            Long id = snowflake.snowflakeId();
            String shortUrl = tinyUrlConfig.getDomain() + NumericConvertUtils.toRandomNumberSystem62(id);
            UrlMapping mapping = new UrlMapping();
            mapping.setId(id);
            mapping.setShortUrl(shortUrl);
            mapping.setUrl(longUrl);
            mapping.setTotalVisits(0L);
            mapping.setIpVisits(0L);
            mapping.setExpireTime(-1L);
            mapping.setName("促销短信url");
            mapping.setCreateId(108L);
            mapping.setUpdateId(108L);
            mapping.setCreateTime(System.currentTimeMillis());
            mapping.setUpdateTime(System.currentTimeMillis());
            urlMappingMapper.insert(mapping);
            bucket.set(mapping.getShortUrl());
        }
        return bucket.get();
    }

    @Override
    public String urlMapping(String shortUrl) {
        return null;
    }

}
