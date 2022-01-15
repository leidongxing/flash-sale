package com.tlyy.sale.api.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author LeiDongxing
 * created on 2022/1/4
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CreateOrderMultiVO extends CreateOrderV2VO {
    private long createTime;
}
