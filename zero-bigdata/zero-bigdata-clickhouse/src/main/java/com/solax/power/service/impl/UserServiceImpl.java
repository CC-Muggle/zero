package com.solax.power.service.impl;

import com.solax.power.dao.UserDao;
import com.solax.power.entity.User;
import com.solax.power.model.req.ReqUserAddDTO;
import com.solax.power.model.res.ResUserDetailDTO;
import com.solax.power.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
