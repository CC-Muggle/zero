package com.github.zero.service.impl;

import org.springframework.stereotype.Service;

import com.github.zero.service.SpringBootService;

@Service("springBootService")
public class SpringBootServiceImpl implements SpringBootService{

	@Override
	public void testHelloWorld() {
		System.out.println("HelloWorld");
	}

}
