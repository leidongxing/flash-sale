package com.tlyy.sale.api.service.url;

import java.util.HashMap;

/**
 * @author LeiDongxing
 * created on 2021/6/20
 */
public class InCreaseStrategy implements IStrategy {
    HashMap<Integer, String> map = new HashMap<>();
    int i = 0;

    @Override
    public String getTinyUrl(String longUrl) {
        map.put(i, longUrl);
        return "http://tinyurl.com/" + i++;
    }

    @Override
    public String urlMapping(String shortUrl) {
        return map.get(Integer.parseInt(shortUrl.replace("http://tinyurl.com/", "")));
    }
}

