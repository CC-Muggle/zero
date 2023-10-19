package com.sunfintech.zero.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication(scanBasePackages = {"com.sunfintech.*.**"})
public class SpringBootStarter {

	public static void main(String[] args) {
//		SpringApplication.run(SpringBootStarter.class, args);
		UUID uuid = UUID.randomUUID();
		System.out.println(uuid);


		new ConcurrentHashMap<>().forEach((key,value)->{

		});
	}
}
