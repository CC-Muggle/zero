package com.github.provider.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import com.github.hystrix.configuration.CircuitBreackerConfiguration;
import com.github.provider.configuration.RestTemplateConfiguration;

@SpringBootApplication(scanBasePackages = {"com.github.provider.*"}, scanBasePackageClasses = {RestTemplateConfiguration.class, CircuitBreackerConfiguration.class})
@EnableEurekaClient
@EnableDiscoveryClient
@EnableCircuitBreaker
public class ServerProviderStarter {

    /**
     * springboot主启动类
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(ServerProviderStarter.class, args);
    }
    
}
