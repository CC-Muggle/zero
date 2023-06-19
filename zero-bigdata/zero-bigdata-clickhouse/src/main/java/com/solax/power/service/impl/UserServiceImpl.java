package com.solax.power.service.impl;

import com.solax.power.dao.UserDao;
import com.solax.power.entity.User;
import com.solax.power.model.res.ResUserDTO;
import com.solax.power.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Resource
    private UserDao userDao;

    @Override
    public ResUserDTO getUser(String account, String password) {
        log.info("尝试使用clickhouse，account：{}，password：{}", account, password);
        List<User> userList = userDao.lambdaQuery().list();
        return null;
    }
}
