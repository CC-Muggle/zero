package com.sunfintech.base.thread;

import java.util.concurrent.*;

/**
 *
 * 线程池的创建，线程池的入参
 *
 * corePoolSize：核心线程数
 * maximumPoolSize：最大可执行线程数
 * keepAliveTime：每个最大可执行线程数存活时间
 * timeUnit：时间单位
 * workQueue：阻塞队列
 * threadFactory：线程创建工厂
 * handler：拒绝策略
 *
 *
 * @author yangcj
 */
public class ThreadPoolDemo {


    public static void main(String[] args) {
//        createThreadPool();

        createExecutorsThreadPool();
    }

    private static void createExecutorsThreadPool() {
        ExecutorService executorService = null;
        // 创建一个固定数量的线程池
        executorService = Executors.newFixedThreadPool(10);
        // 创建一个单缓存任务的线程池
        executorService = Executors.newCachedThreadPool();
        // 创建一个定时执行的任务队列池，与常规线程池无异，但是往阻塞队列里边添加内容时会根据对应的堆二叉树进行排序
        // 注意，SecheduledThread不适合使用ExecutorService去接收，更适合用ScheduledExecutorService.schedule
        executorService = Executors.newScheduledThreadPool(10);
        // 创建一个forkjoinpool的线程池
        executorService = Executors.newWorkStealingPool(10);
    }

    /**
     * 线程池创建
     *
     * 1.养成好习惯，在线程池使用完成的时候请关闭线程池
     * 2.线程池的工作过程：核心线程数-》阻塞队列-》最大线程数-》拒绝策略
     * 3.关于线程池增强：线程池通过内部类Worker来实现线程的装载和执行，通过调整核心线程数（corePoolSize）和最大线程数（maximumPoolSize）以及阻塞队列（workQueue）来判断线程新增还是进入队列
     *     那么，在增强的过程中，只需要将corePoolSize、maximumPoolSize、workQueue.size变更，即可动态实现线程池的大小扩展
     *
     */
    private static void createThreadPool() {
        // 创建基础的
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 20, 10000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(10));

        try {
            for (int i = 0; i < 100; i++) {
                int finalI = i;
                threadPoolExecutor.submit(() -> {
                    System.out.println("线程执行开始--------------------------" + finalI);
                    try {
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("--------------线程执行结束-------------" + finalI);
                });
            }
        } catch (Exception e) {
            System.out.println("refuse");
            e.printStackTrace();
        }
        threadPoolExecutor.shutdown();
    }
}


