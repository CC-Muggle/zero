package com.sunfintech.base.net.socket;

/**
 *
 * NIO模型演示demo
 *
 * NIO核心的三大组件，selector，channel，buffer
 *
 * 1.每一个channel都对应一个buffer，channel不直接与外界交互，而是通过读取buffer中的内容与buffer直接交互
 * 2.selector对应一个线程，而一个线程可以对应多个channel，channel需要注册到selector上
 * 3.程序切换到那个channel是由事件决定的，event是其重要的选择标志
 *
 * @author yangcj
 */
public class NIOHandlerDemo {

    public static void main(String[] args) {

    }
}
