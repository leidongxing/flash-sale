package com.tlyy.sale.exception;

import lombok.Data;

/**
 * @author LeiDongxing
 * created on 2020/5/5
 */
@Data
public class CommonException extends RuntimeException {
    private String code;

    private String message;

    public CommonException(CommonResponseCode code) {
        this.code = code.getCode();
        this.message = code.getMessage();
    }

    public CommonException(CommonResponseCode code,String message) {
        this.code = code.getCode();
        this.message = message;
    }
}
