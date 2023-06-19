package com.solax.power.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.solax.power.dao.mapper.UserMapper;
import com.solax.power.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserDao extends ServiceImpl<UserMapper, User> {
}
