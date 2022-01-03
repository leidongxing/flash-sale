package com.tlyy.sale.api.entity;

import lombok.Data;

import java.util.Date;

@Data
public class ItemStock {
    private Long id;

    private Long stock;

    private Long itemId;

    private Date gmtCreate;

    private Date gmtModified;

    private Boolean deleted;
}