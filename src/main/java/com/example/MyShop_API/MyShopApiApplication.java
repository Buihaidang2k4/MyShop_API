package com.example.MyShop_API;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MyShopApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyShopApiApplication.class, args);
    }
}


