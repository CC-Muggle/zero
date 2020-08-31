package com.sunfintech.hystrix.configuration;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;

@Configuration
public class CircuitBreackerConfiguration {

    /**
     * SpringCloud 用于使用hystrix监控添加的servlet
     * 
     * 
     * 1.将在springboot的内置tomcat中添加servlet的配置,配置到springMVC中
     * 2.注意加载顺序,有点类似于springMVC在配置spring启动的时候有限加载spring配置项
     * 
     * reason:
     *  
     * 
     * @return
     */
    @Bean
    public ServletRegistrationBean<HystrixMetricsStreamServlet> getServlet(){
        HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
        ServletRegistrationBean<HystrixMetricsStreamServlet> registrationBean = new ServletRegistrationBean<>(streamServlet);
        registrationBean.setLoadOnStartup(1);  //系统启动时加载顺序
        registrationBean.addUrlMappings("/hystrix.stream");//路径
        registrationBean.setName("HystrixMetricsStreamServlet");
        return registrationBean;
    }
}
