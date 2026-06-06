package com.github.base.thread;

import java.util.concurrent.*;

/**
 * Java 21 虚拟线程（协程）示例
 *
 * 虚拟线程（Virtual Threads）是JDK 21引入的轻量级线程实现（Project Loom），
 * 它们由JVM管理而非操作系统，可以高效地创建数百万个虚拟线程。
 *
 * 虚拟线程可以在某个线程阻塞时自动让出载体线程（Carrier Thread），
 * 使得少量OS线程即可支撑海量并发任务。
 *
 * @author yangcj
 */
public class CoroutinesDemo {

    public static void main(String[] args) throws Exception {
//        testVirtualThread(10000);
//        testThread(10000);
        testUnion(10000);
    }

    public static void testThread(int count) throws InterruptedException {
        ExecutorService executorService = new ThreadPoolExecutor(10, 20, 60000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        final CountDownLatch latch = new CountDownLatch(count);
        long startTime = System.currentTimeMillis();
        for (int a = 0; a < count; a++) {
            int finalA = a;
            executorService.execute(() -> {
                System.out.println("我第" + finalA + "次怎么去获取线程名称：" + Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        System.out.println("线程消耗时间：" + (System.currentTimeMillis() - startTime) + "ms");
        executorService.shutdown();
    }

    /**
     * 使用Java 21虚拟线程
     * 虚拟线程在IO/睡眠等阻塞操作时会自动释放底层OS线程，实现高效的并发
     */
    public static void testVirtualThread(int count) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(count);
        long startTime = System.currentTimeMillis();
        for (int a = 0; a < count; a++) {
            int finalA = a;
            Thread.ofVirtual()
                .name("VirtualThread-" + finalA)
                .start(() -> {
                    System.out.println("我第" + finalA + "次怎么去获取虚拟线程名称：" + Thread.currentThread().getName());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        latch.countDown();
                    }
                });
        }
        latch.await();
        System.out.println("虚拟线程消耗时间：" + (System.currentTimeMillis() - startTime) + "ms");
    }

    /**
     * 虚拟线程与传统线程池混合使用
     */
    public static void testUnion(int count) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(count);
        long startTime = System.currentTimeMillis();

        // 使用虚拟线程的 ExecutorService (Java 21)
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int a = 0; a < count; a++) {
                int finalA = a;
                executorService.execute(() -> {
                    System.out.println("我第" + finalA + "次怎么去获取虚拟线程名称：" + Thread.currentThread().getName());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        latch.countDown();
                    }
                });
            }
            latch.await();
        }
        System.out.println("虚拟线程消耗时间：" + (System.currentTimeMillis() - startTime) + "ms");
    }
}