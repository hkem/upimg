package com.upimage.upimg;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@MapperScan("com.upimage.upimg.mapper")//使用MapperScan批量扫描所有的Mapper接口；
public class UpimgApplication extends SpringBootServletInitializer {



    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(UpimgApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(UpimgApplication.class, args);
    }



}
