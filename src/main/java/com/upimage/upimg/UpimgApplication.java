package com.upimage.upimg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class UpimgApplication extends SpringBootServletInitializer {



    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(UpimgApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(UpimgApplication.class, args);
    }



}
