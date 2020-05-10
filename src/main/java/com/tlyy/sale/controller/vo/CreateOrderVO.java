package com.tlyy.sale.controller.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author LeiDongxing
 * created on 2020/5/5
 */
@Data
public class CreateOrderVO {
    @NotNull
    private Long itemId;
    @NotNull
    private Long amount;
}
