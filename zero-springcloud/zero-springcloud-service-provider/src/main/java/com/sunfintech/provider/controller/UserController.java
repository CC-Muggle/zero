package com.sunfintech.provider.controller;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.sunfintech.provider.entity.User;

/**
 * 被RestController修饰过的接口都已ResponseBody返回
 * 相当于全局的ResponseBody
 * 
 * 给定统一请求参数,用于接口分类
 * @author yangcj
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    
    /**
     * 随意编写的接口
     * @param id
     * @return
     */
    @GetMapping(value = "/getUser", produces = "application/json;charset=UTF-8")
    @HystrixCommand(fallbackMethod = "getDefaultUser", commandProperties = {
            @HystrixProperty(name = "circuitBreaker.enabled", value = "true"),                                      // 开启断路器
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "10000"),                    // 时间窗口周期
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),                         // 请求总量总阈值
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "60")                        // 错误阈值百分比
    })
    public User getUser(@RequestParam("id") String id) {
        User user = new User();
        if(StringUtils.isBlank(id)) {
            log.info("抛出异常测试熔断器id:{}", id);
            throw new RuntimeException("id不能为空");
        }
        user.setAccount("abc00");
        user.setPassword("F4EA9F46W4FE5AW1G1G3W4FEWAFE5WA3");
        log.info("响应对象user:{}", user);
        return user;
    }
    
    /**
     * 服务降级方法,被@HystrixCommand标注当服务报错或者不可用时,返回响应的方法
     * 
     * tips:
     * 1.方法入参和出参结果要与原有方法一致
     * 2.容易触发警告,在服务宕机时,该降级方案不会生效
     * 
     * @param id
     * @return
     */
    private User getDefaultUser(String id) {
        User user = new User();
        user.setAccount("ERROR");
        user.setPassword("ERROR");
        return user;
    }
}
