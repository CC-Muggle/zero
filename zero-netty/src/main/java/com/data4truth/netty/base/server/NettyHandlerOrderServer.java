package com.data4truth.netty.base.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyHandlerOrderServer {

	public static void main(String[] args) {
		EventLoopGroup boss = new NioEventLoopGroup();
		EventLoopGroup worker = new NioEventLoopGroup();

		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(boss, worker)
				.channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<Channel>() {

					@Override
					protected void initChannel(Channel channel) throws Exception {
						// 在管道新增节点处理
						ChannelPipeline pipeline = channel.pipeline();
						pipeline.addLast(new ChannelInboundHandlerAdapter() {

							@Override
							public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
								System.out.println("inbound ---------- 1");
								ctx.fireChannelRead(msg);
							}

						});
						pipeline.addLast(new ChannelOutboundHandlerAdapter() {
							public void read(ChannelHandlerContext ctx) throws Exception {
								System.out.println("outbound --------- 1");
								ctx.read();
							}

							public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
								System.out.println("outbound --------- 1");
								ctx.write(msg, promise);
							}

							public void flush(ChannelHandlerContext ctx) throws Exception {
								System.out.println("outbound --------- 1");
								ctx.flush();
							}
						});
						pipeline.addLast(new ChannelOutboundHandlerAdapter() {

							public void read(ChannelHandlerContext ctx) throws Exception {
								System.out.println("outbound --------- 2");
								ctx.read();
							}

							public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
								System.out.println("outbound --------- 2");
								ctx.write(msg, promise);
							}

							public void flush(ChannelHandlerContext ctx) throws Exception {
								System.out.println("outbound --------- 2");
								ctx.flush();
							}
						});
						pipeline.addLast(new ChannelInboundHandlerAdapter() {

							@Override
							public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
								System.out.println("inbound ---------- 2");
								ctx.fireChannelRead(msg);
							}

						});
					}
				});
		try {
			// 服务绑定端口并监听内容，sync方法会阻塞直到服务启动绑定完成
			ChannelFuture channelFuture = bootstrap.bind(1883).sync();
			System.out.println("the server has been started");
			// 获取到channel要关闭的通知并阻塞当前线程直到channel被关闭，来自sync方法
			channelFuture.channel().closeFuture().sync();

		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			boss.shutdownGracefully();
			worker.shutdownGracefully();
		}
	}

}
