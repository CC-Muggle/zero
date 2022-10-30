package com.sunfintech.consumer.controller;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sunfintech.consumer.request.AuthReuestDTO;
import com.sunfintech.provider.api.AuthService;

@Controller
@RequestMapping("/api/server/auth")
public class AuthController {
	
	@DubboReference(version = "1.0.0", check = false)
	private AuthService authService;
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
	public String login(@RequestBody AuthReuestDTO dto) {
		return authService.login(dto.getUserName(), dto.getPassword()); 
	}

}
