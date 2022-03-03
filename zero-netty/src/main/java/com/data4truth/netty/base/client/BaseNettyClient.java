package com.data4truth.netty.base.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 *
 * netty基础案例，客户端发送消息
 *
 * @author yangcj
 */
public class BaseNettyClient {

    public static void main(String[] args) {

        Bootstrap bootstrap = new Bootstrap();

        EventLoopGroup worker = new NioEventLoopGroup();

        // 启动响应管道
        bootstrap.group(worker).channel(NioSocketChannel.class).handler(new ChannelInitializer<Channel>() {


            @Override
            protected void initChannel(Channel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();

                // ByteBuf组件，不知道对面会给过来什么的情况下采用的netty统一化载体
                pipeline.addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf s) throws Exception {

                        // 获取可读字节数组
                        byte[] bytes = new byte[s.readableBytes()];
                        s.readBytes(bytes);
                        String response = new String(bytes, "UTF-8");
                        System.out.println("响应的消息：" + response);

                    }
                });
            }
        });

        try {
            // 连接ip+port成功前为阻塞状态
            ChannelFuture future = bootstrap.connect("127.0.0.1", 8900).sync();
            future.addListener((ChannelFutureListener) channelFuture -> {
                if(channelFuture.isSuccess()){

                }
            });
            Channel channel = future.channel();
            while (channel.isActive()){
                System.out.print("请输入消息发送：");
                Scanner scanner = new Scanner(System.in);
                String readIn = scanner.nextLine();
                ByteBuf byteBuf = Unpooled.copiedBuffer(readIn.getBytes(StandardCharsets.UTF_8));

                // channel激活时输入内容
                channel.writeAndFlush(byteBuf);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
