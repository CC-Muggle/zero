package com.data4truth.netty.mqtt.server;

import com.data4truth.netty.mqtt.handler.MqttNettyChannelHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MqttNettyServer {


    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                //等待队列
                .option(ChannelOption.SO_BACKLOG, 100)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                //快速复用
                .childOption(ChannelOption.SO_REUSEADDR,true)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();

                        // MQTT数据解码
                        pipeline.addLast(new MqttDecoder());
                        pipeline.addLast(MqttEncoder.INSTANCE);

                        // MQTT消息数据操作
                        pipeline.addLast(new MqttNettyChannelHandler());
                    }
                });

        try {
            ChannelFuture future = serverBootstrap.bind(1883).sync();
            log.info("netty端口映射1883启动");
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }

    }
}
