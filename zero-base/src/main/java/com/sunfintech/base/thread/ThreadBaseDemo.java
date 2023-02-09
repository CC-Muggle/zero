package com.sunfintech.base.thread;


/**
 * 
 * 
 * 线程基础入门
 * 
 * 
 * 
 * @author yangcj
 *
 */
public class ThreadBaseDemo {

    public static void main(String[] args) {
        Thread thread = new Thread(()->{
            System.out.println("打印一下");
        });

        thread.start();
    }
}
