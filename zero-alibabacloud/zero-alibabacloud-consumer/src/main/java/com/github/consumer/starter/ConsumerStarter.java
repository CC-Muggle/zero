package com.github.consumer.starter;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"com.github.consumer.*"})
@EnableDubbo
@EnableDiscoveryClient
public class ConsumerStarter {
	
	
	public static void main(String[] args) {
		SpringApplication.run(ConsumerStarter.class);
	}
}
