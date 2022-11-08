package com.sunfintech.base.thread;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * 线程池的创建，线程池的入参
 *
 * corePoolSize：核心线程数
 * maximumPoolSize：
 * keepAliveTime：
 * timeUnit：
 * workQueue：
 *
 * @author yangcj
 */
public class ThreadPoolDemo {


    public static void main(String[] args) {
        // 创建基础的
        new ThreadPoolExecutor(10,20,1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    }
}
