package com.tlyy.sale.api.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author LeiDongxing
 * created on 2022/1/2
 */
@EqualsAndHashCode(callSuper = true)
@Getter
public class CreateOrderV3VO extends CreateOrderV2VO {
    private final transient Lock lock = new ReentrantLock();
    private final transient Condition condition = lock.newCondition();
}
