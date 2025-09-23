package com.trick.logs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.trick")
public class LogsServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LogsServiceApplication.class, args);
    }
}
