package com.trick.pile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ComponentScan(basePackages = "com.trick.common")
@EnableAsync
@EnableFeignClients
public class PileServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PileServiceApplication.class, args);
    }

}
