package com.fixmycity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FixMyCityApplication {
    public static void main(String[] args) {
        SpringApplication.run(FixMyCityApplication.class, args);
    }
}
