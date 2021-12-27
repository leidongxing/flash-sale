package com.tlyy.sale.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author LeiDongxing
 * created on 2021/12/18
 */
@Data
public class CreateOrderV2VO {
    @NotNull
    private Long itemId;
    @NotNull
    private Long amount;
    @NotNull
    private Long uid;

    private String token;

    private long enterQueueTime;

    public static final int UN_PROCESS = 1; //未处理
    public static final int PROCESS_PENDING = 2; //处理中
    public static final int PROCESS_FAIL = 3; //处理失败
    public static final int PROCESS_SUCCESS_SOLD_OUT = 4;//处理成功 售罄
    public static final int PROCESS_SUCCESS_DEAL= 10;//处理成功 售罄

    private volatile int redisProcessStatus = UN_PROCESS;
}
