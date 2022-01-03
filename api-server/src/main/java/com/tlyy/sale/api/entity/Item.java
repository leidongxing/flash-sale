package com.tlyy.sale.api.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Item {
    private Long id;

    private String title;

    private BigDecimal price;

    private String description;

    private Integer sales;

    private String imgUrl;

    private Date gmtCreate;

    private Date gmtModified;

    private Boolean deleted;
}