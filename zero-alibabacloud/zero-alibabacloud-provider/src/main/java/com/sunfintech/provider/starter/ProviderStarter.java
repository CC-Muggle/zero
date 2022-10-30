package com.sunfintech.provider.starter;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = { "com.sunfintech.provider.*" })
@EnableDubbo(scanBasePackages =  { "com.sunfintech.provider.*" })
@EnableDiscoveryClient
public class ProviderStarter {

	public static void main(String[] args) {
		SpringApplication.run(ProviderStarter.class, args);
	}
}
