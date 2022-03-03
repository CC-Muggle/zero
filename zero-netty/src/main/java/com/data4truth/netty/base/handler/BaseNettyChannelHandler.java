package com.data4truth.netty.base.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * ChannelHandler是一个父级接口，我们去实现该接口去完成对每个阶段的业务逻辑编写
 * ChannelHandler分为入站（inbound）和出站（outbound）
 *
 * ChannelInboundHandler(入站处理流)
 *
 *
 *
 * @author yangcj
 */
public class BaseNettyChannelHandler extends ChannelInboundHandlerAdapter {


    /**
     * 每个传入的消息都要调用的方法
     *
     * @param ctx 通信上下文，用于传递每个阶段的一个包装类，里边可以认为
     * @param msg 实际接收到的信息
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // netty中承载信息的组件默认情况下都是ByteBuf
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println(byteBuf.toString(Charset.defaultCharset()));

        // 此处只是单纯的写到缓冲流里，需要在readComplete后将所有写入的数据冲刷掉
        ctx.write(byteBuf);
    }

    /**
     *
     * 通知ChannelInboundHandler最后一次channelRead的调用是当前批量读取中的最后一条
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // Unpooled是netty中自主封装的一个全局对象
        // 此处为读取最后一条消息后返回一个空buffer并关闭Channel
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);

    }

    /**
     *
     * 在读取期间产生异常捕获时调用
     *
     * 不捕获异常的情况下，以后会被至责任链的末端，
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
