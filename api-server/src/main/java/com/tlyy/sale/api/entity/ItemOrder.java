package com.tlyy.sale.api.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ItemOrder {
    private Long id;

    private Long userId;

    private Long itemId;

    private BigDecimal itemPrice;

    private Long amount;

    private BigDecimal orderPrice;

    private Date gmtCreate;

    private Date gmtModified;

    private Boolean deleted;
}