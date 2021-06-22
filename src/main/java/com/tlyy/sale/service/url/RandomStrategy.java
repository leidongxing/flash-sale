package com.tlyy.sale.service.url;

import java.util.HashMap;
import java.util.Random;

/**
 * @author LeiDongxing
 * created on 2021/6/20
 */
public class RandomStrategy implements IStrategy{
    HashMap<Integer, String> map = new HashMap<>();
    Random r = new Random();
    int key = r.nextInt(Integer.MAX_VALUE);

    @Override
    public String getTinyUrl(String longUrl) {
        while (map.containsKey(key)) {
            key = r.nextInt(Integer.MAX_VALUE);
        }
        map.put(key, longUrl);
        return "http://tinyurl.com/" + key;
    }

    @Override
    public String urlMapping(String shortUrl) {
        return map.get(Integer.parseInt(shortUrl.replace("http://tinyurl.com/", "")));
    }
}

