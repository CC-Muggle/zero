package com.sunfintech.zero.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.sunfintech.zero.service.SpringBootService;
import com.sunfintech.zero.starter.SpringBootStarter;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringBootStarter.class)
class SpringBootServiceImplTest {

	@Autowired
	private SpringBootService springBootService;
	
	@SuppressWarnings("resource")
	@Test
	public void contextloads() {
		ApplicationContext applicationContext = new AnnotationConfigApplicationContext();
		SpringBootService springBootService = applicationContext.getBean(SpringBootServiceImpl.class);
		springBootService.testHelloWorld();
	}
	
	@Test
	void test() {
		springBootService.testHelloWorld();
	}

}
