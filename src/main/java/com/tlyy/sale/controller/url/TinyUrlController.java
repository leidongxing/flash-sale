package com.tlyy.sale.controller.url;

import com.tlyy.sale.controller.vo.CreateOrderVO;
import com.tlyy.sale.controller.vo.TinyUrlVO;
import com.tlyy.sale.exception.CommonResponse;
import com.tlyy.sale.service.url.TinyUrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LeiDongxing
 * created on 2021/6/20
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class TinyUrlController {

    private final TinyUrlService tinyUrlService;

    /**
     * 创建tiny url
     */
    @PostMapping("/tiny-url")
    public CommonResponse getTinyUrl(@Validated @RequestBody TinyUrlVO vo) {
        return CommonResponse.success(tinyUrlService.getTinyUrl(vo.getUrl()));
    }
}
