package com.tlyy.sale.api.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author LeiDongxing
 * created on 2020/5/5
 */
@Getter
@AllArgsConstructor
public enum CommonResponseCode {
    OK("00000","ok","一切ok"),


    // 用户端错误 一级宏观错误码 A0001
    /**
     *  用户注册错误  二级宏观错误码 A0100
     */
    DISAGREE_PROTOCOL("A0101","disagree protocol","用户未同意隐私协议"),
    UNSUPPORTED_REGION("A0102","unsupported region","注册国家或地区受限"),



    // 系统端错误 一级宏观错误码 B0001
    /**
     *  系统执行超时 二级宏观错误码  B0100
     */
    ORDER_TIME_OUT("B0101","order time out","系统订单处理超时"),

    /**
     * 系统容灾功能被触发 二级宏观错误码 B0200
     */
    RATE_LIMIT("B0210","rate limit","系统限流"),
    SERVICE_DEGRADATION("B0220","service degradation","系统功能降级"),

    /**
     * 系统容灾功能被触发 二级宏观错误码 B0300
     */
    ERROR("B0310","error","系统错误"),

    // 调用第三方服务错误 C0001
    /**
     *  中间件服务出错 二级宏观错误码 C0100
     */
    RPC_ERROR("C0110","rpc error","RPC服务出错"),
    RPC_NOT_FOUND("C0111","rpc not found","RPC服务未找到");

    private final String code;
    private final String message;
    private final String comment;
}
