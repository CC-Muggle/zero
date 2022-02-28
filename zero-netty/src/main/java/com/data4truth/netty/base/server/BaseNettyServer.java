package com.data4truth.netty.base.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.Charset;

/**
 * @author yangcj
 *
 * netty基础案例，服务端接收消息
 *
 * 1.创建boss和worker线程，boss线程用于接收和转发，worker线程注重于业务处理
 * 2.创建启动管道，为管道添加处理信息的节点，即责任链
 * 3.启动服务，并将服务暴露到指定端口，等待端口访问
 *
 */
public class BaseNettyServer {


    public static void main(String[] args) {


        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();


        ServerBootstrap bootstrap = new ServerBootstrap();
        // 服务端绑定boss线程和worker线程
        bootstrap.group(boss, worker)
                // 创建服务暴露通道
                .channel(NioServerSocketChannel.class)
                // 初始化channel业务处理配置
                .childHandler(new ChannelInitializer<Channel>() {

                    @Override
                    protected void initChannel(Channel channel) throws Exception {

                        // 在管道新增节点处理
                        ChannelPipeline pipeline = channel.pipeline();

                        // 输入集
                        pipeline.addLast(new SimpleChannelInboundHandler<ByteBuf>() {

                            @Override
                            protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
                                byte[] bytes = new byte[byteBuf.readableBytes()];
                                byteBuf.readBytes(bytes);
                                System.out.println("响应接收" + new String(bytes, "UTF-8"));

                                // 责任链模式，传递给下一个责任链
                                // 此处重新生成引用的原因是，每次责任链调用了以后不会自动传递给下一条责任节点，而是需要fire
                                // 所以该节点是默认为结束的，所以下一节点不能使用入参作为下一节点开火的入参，必须为当前节点生成的数据对下一节点开火
                                ByteBuf buf = Unpooled.copiedBuffer(byteBuf);
                                channelHandlerContext.fireChannelRead(buf);
                            }
                        });

                        pipeline.addLast(new SimpleChannelInboundHandler<ByteBuf>() {

                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                                // 获取可读字节数组
                                String request = "copy that";
                                System.out.println("响应发送：" + request);

                                // 8是很懂每次都要cp到缓存里
                                ByteBuf byteBuf = Unpooled.copiedBuffer(request.getBytes(Charset.defaultCharset()));

                                // 回写信息
                                ctx.writeAndFlush(byteBuf);
                            }
                        });

                    }
                });

        try {
            // 服务绑定端口并监听内容
            ChannelFuture channelFuture = bootstrap.bind(8900).sync();
            System.out.println("the server has been started");
            // 异步返回结果
            channelFuture.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }
}
