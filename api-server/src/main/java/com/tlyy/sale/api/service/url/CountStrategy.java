package com.tlyy.sale.api.service.url;

import java.util.HashMap;

/**
 * @author LeiDongxing
 * created on 2021/6/20
 */
public class CountStrategy implements IStrategy {

    String chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    HashMap<String, String> map = new HashMap<>();
    int count = 1;

    public String getString() {
        int c = count;
        StringBuilder sb = new StringBuilder();
        while (c > 0) {
            c--;
            sb.append(chars.charAt(c % 62));
            c /= 62;
        }
        return sb.toString();
    }

    @Override
    public String getTinyUrl(String longUrl) {
        String key = getString();
        map.put(key, longUrl);
        count++;
        return "http://tinyurl.com/" + key;
    }

    @Override
    public String urlMapping(String shortUrl) {
        return map.get(shortUrl.replace("http://tinyurl.com/", ""));
    }
}
