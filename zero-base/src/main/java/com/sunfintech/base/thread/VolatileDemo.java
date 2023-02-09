package com.sunfintech.base.thread;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 验证volatile轻量级同步方法
 * 1. volatile能够保证在JMM规范中的可见性,是的其他线程能够及时同步
 * 2. volatile无法保证原子性,但是synchronized可以禁止
 * 3. volatile会禁止运行时的重排序
 * 
 * 
 * volatile如何保证可见性和禁止重排序的
 * 
 * 1.通过内存屏障（读屏障、写屏障）进行禁止重排序
 * 2.写屏障会让写入工作内存的东西及时刷新到主内存中，这样就能通过总线嗅探机制及时通知到各个线程让其刷新工作内存
 * 
 * volatile是如何添加内存屏障的
 * 被volatile修饰的变量在底层会被加上一个ACC_VOLATILE，会在指定的代码段加上内存屏障
 * 
 * 
 * 小tips：
 * 对象实例化需要三个过程
 * 
 * 1.分配内存空间（memory = allocate()）
 * 2.初始化对象（ctorInstance(memory)）
 * 3.引用指向对应得内存空间（instance = memory）
 * 
 * @author yangcj
 *
 */
public class VolatileDemo {
    
    public static void main(String[] args) {
        validateVolatileOrderliness();
    }
    
    /**
     * 验证volatile是否保证有序性
     * 貌似很难测试,因为无法保证可见性,致使的无法让线程之间同步数据
     * 既然如此,可以默认保证有序性,因为保证了可见性那么则必须保证有序性
     */
    public static void validateVolatileOrderliness() {
        TestData data = new TestData();
        
        new Thread(()->{
            for (int i = 0; i < 3; i++) {
                data.testOrderliness();
            }
        }).start();
        
        data.testOrderliness();
    }
    
    /**
     * 验证volatile是否具有可见性
     * 验证:在添加volatile时程序能正确返回,未添加时,进入了while死循环
     * 结论:volatile具有可见性,会将修改的数据及时同步到主内存,使得其他线程可见
     */
    public static void validateVolatileVisiblity() {
        TestData testData = new TestData();
        
        new Thread(() -> {
            System.out.println("开始测试volatile是否保证了可见性");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            testData.updateNumber();
        }, "Thread-volatile-visiblity").start();
        
        
        while (testData.number == 0) {
        }
        
        System.out.println("volatile可见性验证结果 value: " + testData.number);
    }
    
    /**
     * 验证volatile是否具有原子性
     * 验证:添加了volatile的情况下,最终结果应该为20000,但最终结果每次都不一致
     * 结论:volatile不具有原子性,其他数据在修改的时候不能保证整个过程不被干扰
     * 
     * 可使用AtomicInteger使得volatile实现同步
     */
    public static void validateVolatileAtomicity() {
        TestData testData = new TestData();
        
        for (int i = 0; i < 20; i++) {
            new Thread(()->{
                System.out.println("线程启动开始测试volatile是否保证了原子性");
                for (int j = 0; j < 1000; j++) {
                    testData.pulsNumber();
                }
            }, "Thread-volatile-atomicity-" + i).start();
        }
        
        // Thread.activeCount()代表当前活动线程数,除了主线程和后台GC线程,其他都为新增线程
        while (Thread.activeCount() > 2) {
            // 运行中的主线程采取礼让,让其他线程先执行
            Thread.yield();
        }
        
        System.out.println("volatile原子性验证结果 value: " + testData.number);
    }
}

class TestData{
    
    int number = 0;
    
    int pulsOne = 0;
    
    /**
     * 无参构造初始值为零
     */
    volatile AtomicInteger atomicInteger = new AtomicInteger();
    
    public void updateNumber() {
        this.number = 60;
    }
    
    public void pulsNumber() {
        this.number++;
        this.pulsOne++;
    }
    
    public void pulsAtomicNumber() {
        this.atomicInteger.getAndIncrement();
    }
    
    int a,b,c = 0;
    
    public void testOrderliness() {
        a = 1;
        c = a + 3;
        b = a*c;
        
        System.out.println("a:" + a + " b:" + b + " c:" + c);
    }
    
}