package com.github.power.service;

import com.github.power.model.req.ReqUserAddDTO;
import com.github.power.model.res.ResUserDetailDTO;

import java.util.List;

public interface UserService {

    List<ResUserDetailDTO> listUser();

    void addUser(ReqUserAddDTO dto);

}
