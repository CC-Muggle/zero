package com.data4truth.netty.base.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;

import java.nio.charset.Charset;

/**
 *
 * 使用netty实现OIO（BIO）方法
 *
 * 当前使用的是4.1.74版本的netty，OIO已经完全过时了，此处仅仅适用于实现案例
 *
 *
 * @author yangcj
 */
public class NettyBIOServer {

    public static void main(String[] args) {

        final ByteBuf buf = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hello OIO", Charset.defaultCharset()));

        EventLoopGroup group = new OioEventLoopGroup();
        // 新建服务引导
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(group).channel(OioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {

                ChannelPipeline pipeline = socketChannel.pipeline();

                pipeline.addLast(new ChannelInboundHandlerAdapter(){

                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        System.out.println("通道被激活");
                        // 输出缓冲区中的数据，并关闭通道
                        ctx.writeAndFlush(buf.duplicate()).addListener(ChannelFutureListener.CLOSE);
                    }
                });

            }
        });

        try {
            ChannelFuture channelFuture = bootstrap.bind(8080).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }

    }
}
