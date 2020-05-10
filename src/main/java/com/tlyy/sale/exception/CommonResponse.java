package com.tlyy.sale.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.stream.Collectors;

/**
 * @author LeiDongxing
 * created on 2020/5/5
 */
@Setter
@Getter
public class CommonResponse {
    /***
     * 响应信息
     */
    private String message;
    /***
     * 响应码
     */
    private String code;
    /***
     * 响应数据
     */
    private Object data;

    /**
     * 响应时间
     */
    private Long time = System.currentTimeMillis();

    public CommonResponse(String message, String code, Object data) {
        this.message = message;
        this.code = code;
        this.data = data;
    }

    public CommonResponse(String message, String code) {
        this.message = message;
        this.code = code;
        this.data = Collections.emptyMap();
    }

    public CommonResponse(Object data) {
        this.message = CommonResponseCode.OK.getMessage();
        this.code = CommonResponseCode.OK.getCode();
        this.data = data;
    }

    public static CommonResponse success() {
        return new CommonResponse(CommonResponseCode.OK.getMessage(), CommonResponseCode.OK.getCode(), Collections.emptyMap());
    }

    public static CommonResponse fail() {
        return new CommonResponse(CommonResponseCode.ERROR.getMessage(), CommonResponseCode.ERROR.getCode(), Collections.emptyMap());
    }
}
