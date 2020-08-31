package com.sunfintech.consumer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sunfintech.consumer.entity.Business;
import com.sunfintech.consumer.entity.Commodity;
import com.sunfintech.consumer.entity.Order;
import com.sunfintech.consumer.entity.User;
import com.sunfintech.consumer.pojo.UserDTO;
import com.sunfintech.consumer.service.UserMicroService;

@Controller
@RequestMapping("/business")
public class BusinessController {

    private static final Logger log = LoggerFactory.getLogger(BusinessController.class);

//    private static final String URL = "http://zero-provider-8001/user/getUser?id=";

    /**
     * Ribbon调用使用注解@LoadBalance
     * 
     */
    // @Autowired
    // @Qualifier("restTemplate")
    // private RestTemplate restTemplate;

    /**
     * OpenFeign调用
     */
    @Autowired
    private UserMicroService userMicroService;

    @RequestMapping(value = "/buy", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Order buySomething(@RequestBody Business business) {
        log.info("产生交易business:{}", business);
        Order order = new Order();
        
//        User user = restTemplate.getForObject(URL + business.getUserId(), User.class);
        
        UserDTO userDTO = userMicroService.getUser(business.getUserId());
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);

        order.setUser(user);
        order.setCommodity(new Commodity());
        order.setBusiness(business);

        return order;
    }
}
