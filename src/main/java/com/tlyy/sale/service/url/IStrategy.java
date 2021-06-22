package com.tlyy.sale.service.url;

/**
 * @author LeiDongxing
 * created on 2021/6/21
 */
public interface IStrategy {
    String getTinyUrl(String longUrl);

    String urlMapping(String shortUrl);
}
