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

    public static final int UN_PROCESS = 1;
    public static final int PROCESS_PENDING = 2;
    public static final int PROCESS_FAIL = 3;
    public static final int PROCESS_SUCCESS = 10;

    private volatile int redisProcessStatus = UN_PROCESS;
}
