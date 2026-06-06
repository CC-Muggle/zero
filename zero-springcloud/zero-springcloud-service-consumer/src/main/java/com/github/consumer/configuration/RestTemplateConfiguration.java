package com.github.consumer.configuration;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate 配置（Spring Boot 4.0 中 RestTemplate 需手动创建 Bean）
 *
 * 注意：Spring Cloud 2025.x 已移除 Ribbon，负载均衡由 Spring Cloud LoadBalancer 提供。
 * RestTemplate 配合 @LoadBalanced 注解使用 LoadBalancer 实现客户端负载均衡。
 *
 * Spring Boot 4.0 推荐使用 RestClient 替代 RestTemplate：
 * <pre>
 *   @Bean
 *   public RestClient restClient(RestClient.Builder builder) {
 *       return builder.build();
 *   }
 * </pre>
 *
 * @author yangcj
 */
@Configuration
public class RestTemplateConfiguration {

    @Bean(name = "restTemplate")
    @LoadBalanced
    public RestTemplate getCommonRestTemplate() {
        return new RestTemplate();
    }

    /**
     * RestTemplate 工厂方法
     *
     * 支持的 ClientHttpRequestFactory 实现：
     * {@link SimpleClientHttpRequestFactory} 使用 java.net.HttpURLConnection（默认）
     *
     * @param requestFactory HTTP 请求工厂
     * @return RestTemplate 实例
     */
    public RestTemplate getRestTemplate(ClientHttpRequestFactory requestFactory) {
        return new RestTemplate(requestFactory);
    }

    /**
     * 带超时配置的 RestTemplate
     *
     * readTimeout: 读取超时 10s
     * connectionTimeout: 连接超时 60s
     */
    @Bean(name = "simpleRestTemplate")
    @LoadBalanced
    public RestTemplate getSimpleRestTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setReadTimeout(10000);
        requestFactory.setConnectTimeout(60000);
        return getRestTemplate(requestFactory);
    }
}
