package com.tlyy.sale.service.url;

import java.util.HashMap;

/**
 * @author LeiDongxing
 * created on 2021/6/20
 */
public class HashCodeStrategy implements IStrategy {
    HashMap<Integer, String> map = new HashMap<>();

    @Override
    public String getTinyUrl(String longUrl) {
        map.put(longUrl.hashCode(), longUrl);
        return "http://tinyurl.com/" + longUrl.hashCode();
    }

    @Override
    public String urlMapping(String shortUrl) {
        return map.get(Integer.parseInt(shortUrl.replace("http://tinyurl.com/", "")));
    }
}
