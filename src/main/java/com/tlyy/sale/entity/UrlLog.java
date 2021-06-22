package com.tlyy.sale.entity;

import lombok.Data;

/**
 * @author LeiDongxing
 * created on 2021/6/21
 */
@Data
public class UrlLog {
    private Long id;

    private String shortUrl;

    private String ip;

    private Long createTime;
}
