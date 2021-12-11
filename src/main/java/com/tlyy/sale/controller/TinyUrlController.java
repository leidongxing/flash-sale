package com.tlyy.sale.controller;

import com.tlyy.sale.vo.TinyUrlVO;
import com.tlyy.sale.exception.CommonResponse;
import com.tlyy.sale.service.url.TinyUrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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


    @GetMapping("/t/*")
    public void redirect(HttpServletResponse response) throws IOException {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String url = request.getRequestURI();
        String longUrl = tinyUrlService.getLongUrl(url);
        response.sendRedirect(longUrl);
    }
}
