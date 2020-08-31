package com.sunfintech.zero.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@Controller
@RequestMapping(value = "test")
public class SringBootController {

	@PostMapping(value = "/hello")
//	@RequestMapping(value = "/hello", method = RequestMethod.POST)
	public Map<String, Object> testHelloWorld(){
		System.out.println("Hello World");
		Map<String, Object> map = new HashMap<String, Object>(16);
		map.put("word", "Hello World");
		return map;
	}
}
