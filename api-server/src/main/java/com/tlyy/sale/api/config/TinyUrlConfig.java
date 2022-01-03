package com.tlyy.sale.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author LeiDongxing
 * created on 2021/6/22
 */
@Data
@Configuration
@ConfigurationProperties(value = "tiny.url")
public class TinyUrlConfig {
    private String domain;
}
