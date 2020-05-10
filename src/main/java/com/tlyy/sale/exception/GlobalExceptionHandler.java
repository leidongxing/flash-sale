package com.tlyy.sale.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author LeiDongxing
 * created on 2020/5/5
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = CommonException.class)
    public CommonResponse handleCommonException(CommonException e) {
        log.error("common exception:{}", e.getMessage());
        return new CommonResponse(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    public CommonResponse handleException(Exception e) {
        log.error("exception:{}", e.getMessage());
        return CommonResponse.fail();
    }
}
