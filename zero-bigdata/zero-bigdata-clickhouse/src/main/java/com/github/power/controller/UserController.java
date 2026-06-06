package com.github.power.controller;

import com.github.power.model.req.ReqUserAddDTO;
import com.github.power.model.res.ResUserDetailDTO;
import com.github.power.model.res.ResultDTO;
import com.github.power.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.List;

@RestController
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/list")
    public ResultDTO<List<ResUserDetailDTO>> list(){
        return new ResultDTO<List<ResUserDetailDTO>>().ofSuccess(userService.listUser());
    }

    @PostMapping("/add")
    public ResultDTO<Object> add(@RequestBody ReqUserAddDTO dto){
        userService.addUser(dto);
        return new ResultDTO<>().ofSuccess();
    }

}
