package com.tlyy.sale.api.entity;

import lombok.Data;

/**
 * @author LeiDongxing
 * created on 2021/6/21
 */
@Data
public class UrlMapping {
    private Long id;

    private String shortUrl;

    private String url;

    private Long totalVisits;

    private Long ipVisits;

    private Long expireTime;

    private String name;

    private Long createId;

    private Long updateId;

    private Long createTime;

    private Long updateTime;
}
