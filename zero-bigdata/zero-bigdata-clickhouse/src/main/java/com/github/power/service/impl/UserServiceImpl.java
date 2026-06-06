package com.github.power.service.impl;

import com.github.power.dao.UserDao;
import com.github.power.entity.User;
import com.github.power.model.req.ReqUserAddDTO;
import com.github.power.model.res.ResUserDetailDTO;
import com.github.power.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Resource
    private UserDao userDao;

    @Override
    public List<ResUserDetailDTO> listUser() {
        List<User> userList = userDao.lambdaQuery().list();
        return userList.stream().map(value -> {
            ResUserDetailDTO res = new ResUserDetailDTO();
            BeanUtils.copyProperties(value, res);
            return res;
        }).collect(Collectors.toList());
    }

    @Override
    public void addUser(ReqUserAddDTO dto) {
        User user = new User();
        log.info("新增User：{}", dto);
        BeanUtils.copyProperties(dto, user);
        userDao.save(user);
    }
}
