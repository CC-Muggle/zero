package com.sunfintech.provider.service.impl;

import java.util.Objects;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;

import com.sunfintech.provider.api.AuthService;

@DubboService(version = "1.0.0")
@Component
public class AuthServiceImpl implements AuthService{

	@Override
	public String login(String userName, String password) {
		if(Objects.equals(userName, "admin") && Objects.equals(password, "admin")) {
			return "success";
		}
		return "failed";
	}

}
