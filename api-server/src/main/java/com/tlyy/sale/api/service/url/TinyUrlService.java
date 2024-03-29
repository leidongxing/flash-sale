package com.tlyy.sale.api.service.url;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author LeiDongxing
 * created on 2021/6/20
 */
@Service
@RequiredArgsConstructor
public class TinyUrlService {

    private final DefaultStrategy defaultStrategy;

    public String getTinyUrl(String url) {
        return defaultStrategy.getTinyUrl(url);
    }

    public String getLongUrl(String tinyUrl) {
        return defaultStrategy.urlMapping(tinyUrl);
    }

}
