package com.github.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ZeroConsumer {


	public void startUp(){
		Properties properties = new Properties();
		properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-local.solaxtech.info:19092");
		properties.put(ConsumerConfig.GROUP_ID_CONFIG, "zero-kafka-consumer");
		properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());


		for (int i = 0; i < 3; i++){
			new Thread(() -> {
				properties.put(ConsumerConfig.CLIENT_ID_CONFIG, "zero-kafka-consumer-client");
				KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
				consumer.subscribe(Arrays.asList("zero-kafka"));

				while (true){
					ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofSeconds(10));
					for (ConsumerRecord<String, String> record : consumerRecords) {
						System.out.println(Thread.currentThread().getName() + "收到消息：topic=" + record.topic() + ",partition:" + record.partition() + ", key:" + record.key() + ", value:" + record.value());
					}
				}
			}, "consumer-" + i).start();
		}
	}



}