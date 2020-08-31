package com.sunfintech.zero.service.impl;

import org.springframework.stereotype.Service;

import com.sunfintech.zero.service.SpringBootService;

@Service("springBootService")
public class SpringBootServiceImpl implements SpringBootService{

	@Override
	public void testHelloWorld() {
		System.out.println("HelloWorld");
	}

}
