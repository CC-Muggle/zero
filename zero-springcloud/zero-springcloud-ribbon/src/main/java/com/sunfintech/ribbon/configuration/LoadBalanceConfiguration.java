package com.sunfintech.ribbon.configuration;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.netflix.loadbalancer.BestAvailableRule;
import com.netflix.loadbalancer.IPing;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import com.netflix.loadbalancer.RetryRule;
import com.netflix.loadbalancer.RoundRobinRule;
import com.netflix.loadbalancer.ServerList;
import com.netflix.loadbalancer.WeightedResponseTimeRule;

/**
 * 自定义配置Ribbon相关属性
 * 自定义情况下,在main启动方法上标注ribbon服务名,并使用@RibbonClient {@link RibbonClient}进行扫描
 * 注意,Ribbon的configuration必须要加,但不能被spring锁扫描到,只能通过RibbonClient进行扫描
 * 
 * 在Spring Cloud Netflix1.2.0开始,Spring支持使用配置文件进行装配
 * <clientName>代表着在主启动类上标记的@RibbonClient的name属性
 *
 * <clientName>.ribbon.NFLoadBalancerClassName: Should implement ILoadBalancer
 * <clientName>.ribbon.NFLoadBalancerRuleClassName: Should implement IRule
 * <clientName>.ribbon.NFLoadBalancerPingClassName: Should implement IPing
 * <clientName>.ribbon.NIWSServerListClassName: Should implement ServerList
 * <clientName>.ribbon.NIWSServerListFilterClassName: Should implement ServerListFilter
 * 
 * 例如,application.properties下,clientName = "consumer"
 * 
 * consumer.ribbon.NFLoadBalancerRuleClassName=com.netflix.loadbalancer.WeightedResponseTimeRule
 * 
 * {@link IRule} Ribbon的负载均衡规则,用来来获取实例
 * {@link IPing} Ribbon后台用于确认服务是否可用,用于刷新服务列表10s
 * {@link ServerList} Ribbon的服务列表,可以使静态的或者动态的,如果是动态的,过一定时间会刷新和过滤服务列表
 * @author coddl
 *
 */
@Configuration
public class LoadBalanceConfiguration {
    
    /**
     * 该配置填写IRule对应规则的全类名
     * 
     * 轮询:com.netflix.loadbalancer.RoundRobinRule {@link RoundRobinRule}
     * 重试:com.netflix.loadbalancer.RetryRule {@link RetryRule}
     * 随机数:com.netflix.loadbalancer.RandomRule {@link RandomRule}
     * 最大可用com.netflix.loadbalancer.BestAvailableRule {@link BestAvailableRule}
     * 响应时间权重om.netflix.loadbalancer.WeightedResponseTimeRule {@link WeightedResponseTimeRule}
     * 
     * 可实现自定义rule接口实现自定义负载均衡规则
     * 
     */
    @Value("${ribbon.loadBalance.rule:}")
    private String ruleFullName;
    
    /**
     * 通过反射的方式配置ribbon负载均衡规则
     * 主要通过实现IRule接口进行载入
     * 
     * 
     * @return
     * @throws ClassNotFoundException ruleFullName选项未填写或为空字符串时,该异常可能被抛出
     * @throws InstantiationException ruleFullName实例的类没有无参构造方法时,抛出该异常
     * @throws IllegalAccessException 非法访问,不存在对应的无参构造方法
     * @throws ClassCastException 实例化对应的类非IRule实现或AbstractLoadBalancerRule子类抛出该异常
     */
    @Bean
    public IRule getLoadBalanceRule() throws ClassNotFoundException, InstantiationException, IllegalAccessException, ClassCastException {
        // 不填或默认情况下,选择轮询模式
        boolean flag = !Objects.equals(ruleFullName, "") && Objects.nonNull(ruleFullName);
        Class<?> ruleClass = flag ? Class.forName(ruleFullName) : null;
        return flag ? (IRule) ruleClass.newInstance() : new RoundRobinRule();        
    }
}
