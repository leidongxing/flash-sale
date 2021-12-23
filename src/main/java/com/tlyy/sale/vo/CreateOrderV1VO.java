package com.tlyy.sale.vo;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author LeiDongxing
 * created on 2020/5/5
 */
@Data
public class CreateOrderV1VO {
    @NotNull
    private Long itemId;
    @NotNull
    @Min(value = 1, message = "数量信息不正确")
    @Max(value = 1000, message = "数量信息不正确")
    private Long amount;
    @NotNull
    private Long uid;

    private String token;
}