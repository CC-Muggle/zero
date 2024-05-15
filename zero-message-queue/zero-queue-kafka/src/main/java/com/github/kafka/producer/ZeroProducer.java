package com.github.kafka.producer;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class ZeroProducer {

	public static void main(String[] args) throws ExecutionException, InterruptedException {
		Properties properties = new Properties();
		properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-local.solaxtech.info:19092");
		properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		Producer<String, String> producer = new KafkaProducer<String, String>(properties);
		for (int i = 0; i < 100; i++){
			ProducerRecord<String, String> record = new ProducerRecord<>("zero-kafka", new Random().nextInt(10), System.currentTimeMillis(), "testConsumer", "{\"code\":" + i + "}");
			RecordMetadata recordMetadata = producer.send(record).get();
			System.out.println("消息发送topic:"+ recordMetadata.topic() + ",发送的partition:" + recordMetadata.partition());
		}
	}

}
