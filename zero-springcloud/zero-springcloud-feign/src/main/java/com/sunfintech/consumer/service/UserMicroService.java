package com.sunfintech.consumer.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sunfintech.consumer.pojo.UserDTO;
import com.sunfintech.consumer.service.fallback.UserFallbackServiceImpl;

@FeignClient(value = "zero-provider-8001", fallback = UserFallbackServiceImpl.class)
public interface UserMicroService {

    @GetMapping(value = "/user/getUser", produces = "application/json;charset=UTF-8")
    UserDTO getUser(@RequestParam("id") String id);
}
