package com.solax.power.service;

import com.solax.power.model.req.ReqUserAddDTO;
import com.solax.power.model.res.ResUserDetailDTO;

import java.util.List;

public interface UserService {

    List<ResUserDetailDTO> listUser();

    void addUser(ReqUserAddDTO dto);

}
