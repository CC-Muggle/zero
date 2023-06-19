package com.solax.power.controller;

import com.solax.power.model.res.ResUserDTO;
import com.solax.power.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/login")
    public ResUserDTO login(){
        return userService.getUser(null, null);
    }

}
