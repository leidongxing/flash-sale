package com.tlyy.sale.api.service.url;

import com.tlyy.sale.api.config.TinyUrlConfig;
import com.tlyy.sale.api.entity.UrlMapping;
import com.tlyy.sale.api.mapper.UrlMappingMapper;
import com.tlyy.sale.api.util.NumericConvertUtils;
import com.tlyy.sale.api.util.SnowflakeByHutool;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author LeiDongxing
 * created on 2021/6/21
 */
@Component
@RequiredArgsConstructor
public class DefaultStrategy implements IStrategy {
    //    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;

    private final UrlMappingMapper urlMappingMapper;

    private final TinyUrlConfig tinyUrlConfig;

    private final SnowflakeByHutool snowflake;

    @Override
    public String getTinyUrl(String longUrl) {
//        RBucket<String> bucket = redissonClient.getBucket(longUrl);
        String value = stringRedisTemplate.opsForValue().get(longUrl);
        if (Objects.isNull(value)) {
            Long id = snowflake.snowflakeId();
            String shortUrl = tinyUrlConfig.getDomain() + "/t/" + NumericConvertUtils.toRandomNumberSystem62(id);
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
            stringRedisTemplate.opsForValue().set(longUrl, shortUrl);
//            bucket.set(mapping.getShortUrl());
        }
//        return bucket.get();
        return value;
    }

    @Override
    public String urlMapping(String shortUrl) {
        return urlMappingMapper.selectUrlByShortUrl(tinyUrlConfig.getDomain() + shortUrl);
    }

}
