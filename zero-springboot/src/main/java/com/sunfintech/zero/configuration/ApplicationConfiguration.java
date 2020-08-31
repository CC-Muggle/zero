package com.sunfintech.zero.configuration;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "zero.system")
public class ApplicationConfiguration {

    @Value("${string.value}")
    private String testStringValue;
    
    @Value("#{'${list.value}'.spilt(',')}")
    private List<String> testListValue;
    
    @Value("#{'${map.value}'}")
    private Map<String, String> testMapValue;
}
