package com.tlyy.sale.vo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author LeiDongxing
 * created on 2021/6/22
 */
@Data
public class TinyUrlVO {
    @NotEmpty
    private String url;
}
