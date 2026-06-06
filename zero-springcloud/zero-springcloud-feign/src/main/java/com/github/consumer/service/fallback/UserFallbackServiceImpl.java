package com.github.consumer.service.fallback;

import org.springframework.stereotype.Component;

import com.github.consumer.pojo.UserDTO;
import com.github.consumer.service.UserMicroService;

/**
 * hystrix集成feign做服务降级
 * @author coddl
 *
 */
@Component
public class UserFallbackServiceImpl implements UserMicroService {

    @Override
    public UserDTO getUser(String id) {
        UserDTO userDTO = new UserDTO();
        userDTO.setAccount("ERROR-feign");
        userDTO.setPassword("ERROR-feign");
        userDTO.setName("小伙汁,服务暂时不可用请稍后重试");
        return userDTO;
    }

}
