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
 * 与服务端建立连接，输入需要发送的消息
 *
 * 入站在客户端的角度讲，数据由服务端流向客户端称之为入站，反之则称为出站
 *
 *
 * @author yangcj
 */
public class BaseNettyClient {

    public static void main(String[] args) {

        //客户端引导
        Bootstrap bootstrap = new Bootstrap();

        EventLoopGroup worker = new NioEventLoopGroup();

        // 启动响应管道
        bootstrap.group(worker).channel(NioSocketChannel.class).handler(new ChannelInitializer<Channel>() {


            @Override
            protected void initChannel(Channel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();

                // ByteBuf组件，不知道对面会给过来什么的情况下采用的netty统一化载体
                // ChannelHandler的执行顺序是由它们被添加的顺序所决定的
                pipeline.addLast(new SimpleChannelInboundHandler<ByteBuf>() {

                    // ChannelHandlerContext用于绑定handler与责任链的关系，同时也可以用于获取底层channel

                    @Override
                    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf s) throws Exception {

                        // 这个方式写数据消息会从消息的末端
//                        channelHandlerContext.channel().writeAndFlush(s);

                        // 这个方式写数据消息会
//                        channelHandlerContext.writeAndFlush(s);


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
                    System.out.println("添加监听成功");
                }
            });

            // 检查通道是否处于可用状态，若可用，则键入可发送的消息
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
