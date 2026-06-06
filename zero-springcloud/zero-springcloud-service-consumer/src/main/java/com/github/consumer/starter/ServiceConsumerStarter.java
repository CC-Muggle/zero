package com.github.consumer.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import com.github.consumer.configuration.RestTemplateConfiguration;
import com.github.hystrix.configuration.CircuitBreackerConfiguration;
import com.github.ribbon.configuration.LoadBalanceConfiguration;

@SpringBootApplication(scanBasePackages = {"com.github.consumer.*"}, scanBasePackageClasses = {RestTemplateConfiguration.class, CircuitBreackerConfiguration.class})
@EnableEurekaClient
@EnableDiscoveryClient
@RibbonClient(name = "consumer", configuration = {LoadBalanceConfiguration.class})
@EnableFeignClients(basePackages = {"com.github.consumer.*"})
@EnableCircuitBreaker
public class ServiceConsumerStarter extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ServiceConsumerStarter.class);
    }
    
    public static void main(String[] args) {
        SpringApplication.run(ServiceConsumerStarter.class, args);
    }
}
