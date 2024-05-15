package com.data4truth.netty.mqtt.handler;

import com.data4truth.netty.mqtt.MqttGlobalContext;
import com.data4truth.netty.mqtt.MqttSessionContext;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.mqtt.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@ChannelHandler.Sharable
public class MqttNettyChannelHandler extends ChannelInboundHandlerAdapter {

    /**
     * String对应topic，
     * 用于保存topic和channel的映射关系
     */
    private final MqttSessionContext context = new MqttSessionContext();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof MqttMessage) {
            Channel channel = ctx.channel();
            MqttMessage message = (MqttMessage) msg;

            MqttMessageType messageType = message.fixedHeader().messageType();
            log.info("MQTT接收到的发送类型===》{}",messageType);
            MqttMessageHandler handler = new MqttMessageHandler();
            log.info("MQTT receive：{}", message);
            switch (messageType) {
                // 建立连接
                case CONNECT:
                    handler.connAck(channel, message);
                    context.setChannel(ctx);
                    MqttGlobalContext.register(context);
                    break;
                // 发布消息
                case PUBLISH:
                    handler.pubAck(channel, message);
                    // 消息广播至所有订阅了当前topic的所有channel
                    break;
                // 订阅主题
                case SUBSCRIBE:
                    // 订阅后将channel和topic进行绑定
                    handler.subAck(channel, message);
                    break;
                // 退订主题
                case UNSUBSCRIBE:
                    handler.unsubAck(channel, message);
                    break;
                // 心跳包
                case PINGREQ:
                    handler.pingResp(channel, message);
                    break;
                // 断开连接
                case DISCONNECT:
                    break;
                // 确认收到响应报文,用于服务器向客户端推送qos1/qos2后，客户端返回服务器的响应
                case PUBACK:
                    break;
                // qos2类型，发布收到
                case PUBREC:
                    break;
                // qos2类型，发布释放响应
                case PUBREL:
                    break;
                // qos2类型，发布完成
                case PUBCOMP:
                    handler.pubComp(channel, message);
                    break;
                default:
                    if (log.isDebugEnabled()) {
                        log.debug("Nonsupport server message  type of '{}'.", messageType);
                    }
                    break;
            }
        }
    }

    /**
     * 发布
     *
     * 发布得通过已存在的topic列表查找出对应的channel，在通过这个channel将其数据输出
     *
     */
    public void publish(MqttMessage message){
        MqttPublishMessage mqttPublishMessage = (MqttPublishMessage) message;
        byte[] headBytes = new byte[mqttPublishMessage.payload().readableBytes()];
        mqttPublishMessage.payload().readBytes(headBytes);
        MqttGlobalContext.discover(context.getSessionId());
    }

    /**
     * 注册
     *
     * 当一个连接通过topic进行订阅了以后，需要为当前的channel与单个topic形成一一对应的关系
     *
     * 也就是说能够通过topic获取channel，也能通过channel去获取topic
     */
    public void register(MqttMessage message){
        MqttSubscribeMessage mqttSubscribeMessage = (MqttSubscribeMessage) message;
        MqttMessageIdVariableHeader messageIdVariableHeader = mqttSubscribeMessage.variableHeader();
        //	构建返回报文， 可变报头
        Set<String> topics = mqttSubscribeMessage.payload().topicSubscriptions().stream().map(MqttTopicSubscription::topicName).collect(Collectors.toSet());
        context.subscribe(topics.toArray(String[]::new));
    }


}

