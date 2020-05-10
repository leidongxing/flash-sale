package com.tlyy.sale;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author LeiDongxing
 * created on 2020/3/10
 */
@SpringBootApplication
@MapperScan(basePackages = {"com.tlyy.sale.mapper"})
public class Application {
    public static void main(String[] args){
        SpringApplication.run(Application.class, args);
    }
}
