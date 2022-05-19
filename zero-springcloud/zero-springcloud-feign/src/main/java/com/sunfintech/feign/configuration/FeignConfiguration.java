package com.sunfintech.feign.configuration;

import org.springframework.context.annotation.Configuration;

import feign.Logger;

/**
 * @author yangcj
 */
@Configuration
public class FeignConfiguration {

    public Logger.Level getLoggerLevel(){
        return Logger.Level.FULL;
    }
}
