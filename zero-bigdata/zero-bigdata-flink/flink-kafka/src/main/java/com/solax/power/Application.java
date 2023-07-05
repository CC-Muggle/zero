package com.solax.power;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.solax.power.*")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}