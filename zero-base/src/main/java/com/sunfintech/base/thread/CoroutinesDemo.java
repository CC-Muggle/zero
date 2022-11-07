package com.sunfintech.base.thread;

import co.paralleluniverse.fibers.Fiber;

import java.util.concurrent.*;

/**
 *
 * java8协程简单实现
 *
 * 协程：又称作虚拟线程，他是存在于线程中的一个子线程，他可以实现多个方法的伪并行执行（实际上就是串行）
 *
 * 协程可以在方法1执行到某一段阻塞的情况下，将执行权交于其他协程并记录下自己的checkPoint，当再次获取执行权的时候会从checkPoint再次执行
 *
 * @author yangcj
 */
public class CoroutinesDemo {

    public static void main(String[] args) throws Exception {
//        testFiber(10000);
//        testThread(10000);
        testUnion(10000);
    }

    public static void testThread(int count) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        final CountDownLatch latch = new CountDownLatch(count);
        long startTime = System.currentTimeMillis();
        for (int a = 0; a < count; a++){
            int finalA = a;
            executorService.execute(()-> {
                System.out.println("我第" + finalA + "次怎么去获取线程名称：" + Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        System.out.println("线程消耗时间：" + (System.currentTimeMillis() - startTime) + "ms");
        executorService.shutdown();
    }

    /**
     * 协程创建
     * @param count
     * @throws InterruptedException
     */
    public static void testFiber(int count) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(count);
        long startTime = System.currentTimeMillis();
        for (int a = 0; a < count; a++){
            int finalA = a;
            new Fiber<Void>("Caller" + a, () -> {
                System.out.println("我第" + finalA + "次怎么去获取协程名称");
                Fiber.sleep(1000);

                latch.countDown();
            }).start();
        }
        latch.await();
        System.out.println("协程消耗时间：" + (System.currentTimeMillis() - startTime) + "ms");
    }

    public static void testUnion(int count) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(count);
        long startTime = System.currentTimeMillis();
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        int threadCount = count / 100;
        for (int a = 0; a < 100; a++) {
            executorService.execute(()->{
                System.out.println("开启" + Thread.currentThread().getName() + "携程组");
                for (int b = 0; b < threadCount + 1; b++) {
                    int finalB = b;
                    new Fiber<Void>("fiber" + b, ()->{
                        System.out.println("我第" + finalB + "次怎么去获取协程名称");
                        Fiber.sleep(1000);
                        latch.countDown();
                    }).start();
                }
            });
        }
        latch.await();
        System.out.println("协程消耗时间：" + (System.currentTimeMillis() - startTime) + "ms");
        executorService.shutdown();
    }
}
