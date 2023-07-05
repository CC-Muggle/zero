package com.solax.power.config;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.kafka")
public class FlinkKafkaConfig {

    @Value("${bootstrap-servers}")
    private String bootstrapServers;
    @Value("${}")
    private String deserializer;

    public KafkaSource<String> getKafkaSource(){
        return KafkaSource.<String>builder()
                .setBootstrapServers(bootstrapServers)
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();
    }

}
