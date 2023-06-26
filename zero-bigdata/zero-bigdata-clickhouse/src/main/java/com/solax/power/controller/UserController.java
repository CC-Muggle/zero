package com.solax.power.controller;

import com.solax.power.model.req.ReqUserAddDTO;
import com.solax.power.model.res.ResUserDetailDTO;
import com.solax.power.model.res.ResultDTO;
import com.solax.power.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
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
