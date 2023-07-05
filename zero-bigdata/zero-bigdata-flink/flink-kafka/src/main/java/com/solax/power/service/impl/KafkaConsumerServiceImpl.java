package com.solax.power.service.impl;

import com.solax.power.service.KafkaConsumerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class KafkaConsumerServiceImpl implements KafkaConsumerService {


    @Resource
    private StreamExecutionEnvironment streamExecutionEnvironment;

    public void flinkKafkaListener(){
        streamExecutionEnvironment.
    }

}
