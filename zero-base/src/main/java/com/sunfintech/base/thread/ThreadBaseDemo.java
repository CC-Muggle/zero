package com.sunfintech.base.thread;

public class ThreadBaseDemo {

    public static void main(String[] args) {
        Thread thread = new Thread(()->{
            System.out.println("打印一下");
        });

        thread.start();
    }
}
