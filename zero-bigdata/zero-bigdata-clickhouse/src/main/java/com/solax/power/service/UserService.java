package com.solax.power.service;

import com.solax.power.entity.User;
import com.solax.power.model.res.ResUserDTO;

public interface UserService {

    ResUserDTO getUser(String account, String password);

}
