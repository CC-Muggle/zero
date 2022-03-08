package com.sunfintech.base.io;

import java.nio.IntBuffer;

/**
 *
 * java原生nio缓冲区基本案例
 *
 * Buffer本身是一个内存块或者说是缓冲区，本身具有读或写双向流输出
 * Channel也是一样的
 *
 * 在NIO模型中，用户不会直接与channel进行交互，而是将数据写到缓冲区buffer，channel再经过selector与buffer进行交互
 * Buffer是实现NIO零拷贝的重要关键，访问者是将数据写入到buffer中，当写完或者写满后，会改变channel的状态，让selector进行读取
 * 零拷贝：
 *      一般情况下，要从磁盘读取文件到网络接口
 *          将文件放入内核缓冲区->将内核缓冲区的数据cp到应用程序缓冲区中->将应用程序缓冲区cp到socket缓冲区中->将socket缓冲区的东西发送到网卡缓冲区
 *      零拷贝并不是真正意义上的零次拷贝，而是减少冗余的拷贝次数，比如，应用程序在自己内存之外的区域开辟一个内存空间，只需要将文件拷贝到该内存够空间即可
 *          将文件放入指定的内存够空间->将改缓冲区的数据发送给网卡
 *
 * Buffer内容介绍
 *
 * position：当前位置
 * limit：每次能够读取的极限，但是极限不能够大于capacity
 * mark：
 * capacity：容量，当缓冲区被创建的时候就已经固定不可更改了
 *
 *
 * @author yangcj
 */
public class BufferDemo {


    public static void main(String[] args) {
        IntBuffer intBuffer = IntBuffer.allocate(5);

        // 往缓冲区中添加元素
        for (int i = 0; i < intBuffer.capacity(); i++){
            intBuffer.put(i * 3);
        }

        // 缓冲区读写切换
        intBuffer.flip();

        // 从缓冲区获取数据
        for (int i = 0; i < intBuffer.capacity(); i++){
            System.out.println(intBuffer.get());
        }

        // 清楚缓冲区，仅仅只是将相关下标重置，但是没有清楚缓冲内容
        intBuffer.clear();

    }
}
