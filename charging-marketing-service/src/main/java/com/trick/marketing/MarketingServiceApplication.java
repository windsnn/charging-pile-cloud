package com.trick.marketing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = "com.trick")
public class MarketingServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MarketingServiceApplication.class, args);
    }
}
