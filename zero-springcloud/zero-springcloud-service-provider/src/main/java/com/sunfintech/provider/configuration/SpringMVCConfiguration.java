package com.sunfintech.provider.configuration;

import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

@Configuration
public class SpringMVCConfiguration{

    @Bean
    public HttpMessageConverters additionalConverters() {
        // JSON 转换器
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        HttpMessageConverter<?> additionalJsonConverter = new MappingJackson2HttpMessageConverter(objectMapper);
        
        // XML 转换器
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.setSerializationInclusion(Include.NON_NULL);
        HttpMessageConverter<?> additionalXmlConverter = new MappingJackson2XmlHttpMessageConverter(xmlMapper);
        return new HttpMessageConverters(additionalJsonConverter, additionalXmlConverter);
    }
    
}
