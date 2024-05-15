package com.data4truth.netty.mqtt;

import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * session上下文
 *
 * 通过传递context的形式，将数据上下传递，是个大对象
 */
@Data
public class MqttSessionContext {

	/**
	 * sessionId
	 */
	private UUID sessionId;

	/**
	 * 当前连接已订阅的topic, 同时，
	 */
	private ConcurrentHashMap<String, String> subscribeTopics;

	private ChannelHandlerContext channel;

	public MqttSessionContext(){
		sessionId = UUID.randomUUID();
		subscribeTopics = new ConcurrentHashMap<>();
	}

	public void subscribe(String... topics){
		for (String topic : topics) {
			subscribeTopics.putIfAbsent(topic, topic);
		}
	}

}
