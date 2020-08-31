package com.sunfintech.eureka.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
@EnableDiscoveryClient
public class EurekaServerProvider {

    public static void main(String[] args) {
        for (String string : args) {
            System.out.println("外部传入参数" + string);
        }
        SpringApplication.run(EurekaServerProvider.class, args);
    }
}
