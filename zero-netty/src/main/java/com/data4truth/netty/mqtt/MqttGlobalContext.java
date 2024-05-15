package com.data4truth.netty.mqtt;

import lombok.Data;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class MqttGlobalContext {

	private static final ConcurrentHashMap<UUID, MqttSessionContext> globalSession = new ConcurrentHashMap<>();

	private static final ConcurrentHashMap<String, ConcurrentHashMap<UUID, MqttSessionContext>> topicMapping = new ConcurrentHashMap<>();

	/**
	 * 将对象注册到全局变量里边
	 * @param context
	 */
	public static void register(MqttSessionContext context){
		globalSession.put(context.getSessionId(), context);
	}

	public static MqttSessionContext discover(UUID sessionId){
		return globalSession.get(sessionId);
	}

	public static Set<MqttSessionContext> discover(String topic){
		return discover();
	}


	/**
	 * 将对象注册到全局变量里边
	 * @param context
	 */
	public static void subscribe(String topic, MqttSessionContext context){
		ConcurrentHashMap<UUID, MqttSessionContext> contextMapping = topicMapping.putIfAbsent(topic, new ConcurrentHashMap<>() );
		contextMapping.put(context.getSessionId(), context);
	}

}
