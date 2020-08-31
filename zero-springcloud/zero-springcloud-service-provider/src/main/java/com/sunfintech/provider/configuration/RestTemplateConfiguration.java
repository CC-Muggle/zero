package com.sunfintech.provider.configuration;

import org.springframework.cloud.netflix.ribbon.RibbonClientHttpRequestFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.InterceptingClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate集中设置
 * @author yangcj
 *
 */
@Configuration
public class RestTemplateConfiguration {

    
    @Bean(name = "restTemplate")
    public RestTemplate getCommonRestTemplate() {
        return new RestTemplate();
    }
    
    /**
     * ClientHttpRequestFactory相关子类以及注入
     * 
     * {@link BufferingClientHttpRequestFactory} BufferingClientHttpRequestFactory
     * 
     * 
     * {@link InterceptingClientHttpRequestFactory} InterceptingClientHttpRequestFactory
     * 
     * 
     * {@link HttpComponentsClientHttpRequestFactory} HttpComponentsClientHttpRequestFactory
     * 
     * 
     * {@link OkHttp3ClientHttpRequestFactory} 
     * OkHttp3ClientHttpRequestFactory 采用OKhttp作为内部实现进行http调用
     * 
     * {@link RibbonClientHttpRequestFactory} RibbonClientHttpRequestFactory
     * 
     * 
     * {@link SimpleClientHttpRequestFactory} SimpleClientHttpRequestFactory
     * 
     * @param requestFactory
     * @return
     */
    public RestTemplate getRestTemplate(ClientHttpRequestFactory requestFactory) {
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        return restTemplate;
    }
    
    /**
     * RestTemplate基本样例
     * 
     * SimpleClientHttpRequestFactory 使用java.net原生包进行http请求
     * 
     * bufferRequestBody 是否采用缓冲流进行数据缓冲,默认true
     * outputStreaming 是否将基础模式设置为输出流模式,默认true
     * 
     * chunkSize 每次读入流的数据大小,默认size是4096
     * readTimeout 读取资源超时
     * connectionTimeout 连接建立超时时间
     * 
     * Don`t touch
     * Proxy 代理类,设置会被覆盖,没啥作用
     * AsyncListenableTaskExecutor 异步监听响应线程池
     * 
     * @return
     */
    @Bean(name = "simpleRestTemplate")
    public RestTemplate getSimpleRestTemplate() {
        //可以对http请求进行简单的设置
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        // 资源读取超时时间设置
        requestFactory.setReadTimeout(10000);
        // 连接超时时间设置
        requestFactory.setConnectTimeout(60000);
        
        return getRestTemplate(requestFactory);
        
    }
}
