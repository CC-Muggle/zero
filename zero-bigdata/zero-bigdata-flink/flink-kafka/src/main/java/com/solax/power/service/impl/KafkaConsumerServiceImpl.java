package com.solax.power.service.impl;

import com.solax.power.service.KafkaConsumerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
@Slf4j
public class KafkaConsumerServiceImpl implements KafkaConsumerService {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootStrapServers;
    @Resource
    private StreamExecutionEnvironment streamExecutionEnvironment;

    @PostConstruct
    public void flinkKafkaListener(){
        log.info("打个日志看看有没有被挂起");
        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
                .setBootstrapServers(bootStrapServers)
                .setTopics("test")
                .setGroupId("my-group")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();
        DataStreamSource<String> dataStreamSource = streamExecutionEnvironment.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "kafkaSource");

        // 简单算子
        dataStreamSource.print();

        try {
            // 执行无界流监听
            streamExecutionEnvironment.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
        }
    }

}
