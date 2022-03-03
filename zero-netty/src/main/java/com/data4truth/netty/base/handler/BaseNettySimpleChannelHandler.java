package com.data4truth.netty.base.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.Charset;

/**
 *
 * netty提供的简单入站handler
 *
 * @author yangcj
 */
public class BaseNettySimpleChannelHandler extends SimpleChannelInboundHandler<ByteBuf> {


    /**
     *
     * 当通道被建立的情况下会调用一次
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("通道被激活");
        ctx.writeAndFlush(Unpooled.copiedBuffer("netty rocks", Charset.defaultCharset()));
    }

    /**
     *
     * 与ChannelInboundHandler中的channelRead相似，只不过在此基础封装了一层，不属于类似泛型的请求会被过滤
     * simpleInboundChannelHandler中会对入参的消息进行释放，所以当你需要fire下一个handler的情况下需要资助填写参数
     *
     *
     * @param channelHandlerContext
     * @param byteBuf
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        System.out.println("netty接收到的内容" + byteBuf.toString(Charset.defaultCharset()));
    }

    /**
     *
     * 懂的都懂
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
