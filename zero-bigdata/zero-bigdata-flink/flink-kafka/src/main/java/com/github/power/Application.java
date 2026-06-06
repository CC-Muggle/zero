package com.github.power;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.github.power.*")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}