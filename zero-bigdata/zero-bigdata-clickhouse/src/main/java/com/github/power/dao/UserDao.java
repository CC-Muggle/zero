package com.github.power.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.power.dao.mapper.UserMapper;
import com.github.power.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserDao extends ServiceImpl<UserMapper, User> {
}
