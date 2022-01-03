package com.tlyy.sale.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author LeiDongxing
 * created on 2020/3/10
 */
@SpringBootApplication
@MapperScan(basePackages = {"com.tlyy.sale.api.mapper"})
public class ApiApplication {
    public static void main(String[] args){
        SpringApplication.run(ApiApplication.class, args);
    }
}
