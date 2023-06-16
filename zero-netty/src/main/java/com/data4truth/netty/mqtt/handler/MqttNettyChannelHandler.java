package com.data4truth.netty.mqtt.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.mqtt.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static io.netty.handler.codec.mqtt.MqttQoS.AT_LEAST_ONCE;
import static io.netty.handler.codec.mqtt.MqttQoS.AT_MOST_ONCE;

@Slf4j
@ChannelHandler.Sharable
public class MqttNettyChannelHandler extends ChannelInboundHandlerAdapter {

    private static final Collection<Channel> clientList = new HashSet();
    private static final Map<String,Object> msgMap = new HashMap<>();

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
                    break;
                // 发布消息
                case PUBLISH:
                    handler.pubAck(channel, message);
                    break;
                // 订阅主题
                case SUBSCRIBE:
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


}

