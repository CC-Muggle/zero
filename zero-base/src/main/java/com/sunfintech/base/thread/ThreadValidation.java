package com.sunfintech.base.thread;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * 线程的基本操作
 * 
 * 1.实现线程的创建
 * 2.wait和notify已经notifyAll
 * 3.notify和sleep的区别
 * 
 * @author yangcj
 *
 */
public class ThreadValidation {

    public static void main(String[] args) {
        validateWaitAndSleep();
    }

    /**
     * 验证wait和sleep的区别
     * 
     * wait方法会将当前持有该对象锁的线程进入等待状态，并将对象锁进行释放
     * sleep方法会将当前线程进入睡眠，但是不会释放对象锁（因为他都不知道该释放谁的对象锁），所以一旦被synchronized修饰，直接阻塞所有线程
     * 
     * 一个比较有意思的现象，sleep能够勉强的让对象达成可见性
     * 主要的原因就是因为被重排序了，将对象信息提交到物理内存排到sleep之前，所以执行以下代码可以看到两种结果
     * 一种是person被完整打印，一种是name为空，age为18
     * 
     * 首先sleep在synchronized修饰块中不能够释放对象锁，因此，可以一瞬间阻塞所有线程（危险警告）
     * 其次，sleep是针对线程进行操作，而不进行对象操作，所以不能够保证程序执行的可见性，原子性和可排序性
     * 最后，sleep能起到一定的资源释放（有可能是假象），让给其他线程资源
     * 
     */
    public static void validateWaitAndSleep() {
        Person person = new Person();
        new Thread(() -> {
            // 使用wait方法是必须要加synchronized否则不报java.lang.IllegalMonitorStateException
            System.out.println("设置了姓名为JUC");
            person.name = "JUC";
            // 保证锁定该对象所有的线程处于被唤醒状态
//                person.notifyAll();
//                Thread.sleep(10000);
            System.out.println("爷青回");
        }).start();
        
        if (Objects.equals(person.name, "JUC")) {
            System.out.println(person.name);
        } else {
            System.out.println("不符合爸爸的预期");
//                Thread.sleep(5000);
        }
        person.age = 18;
        System.out.println("设置了年龄为18岁");
//        person.notifyAll();
//        synchronized (person) {
//            
//        }
        System.out.println(person);
    }
    
    

    /**
     * 验证wait和notify作用 
     * 
     * 一般情况下所有线程会对该对象进行锁竞争，竞争到对象锁的线程开始执行
     * 调用wait会使有用对象锁的线程进入等待池，等待被唤醒。
     * 调用notify或者notifyAll会唤醒等待池中的一个或所有线程
     * 
     * 方法wait使当前线程处于停滞状态进入阻塞状态
     * 方法notify随机唤醒处于阻塞状态的线程中的一个（线程越多，随机性越强，最好线程和线程之间不要有影响）
     * 方法notifyAll唤醒所有处于阻塞状态的线程（注意羊群效应）
     * 
     * 注意死锁，无条件wait很容易导致死锁，导致线程长时间阻塞，主线程不能关闭
     * 
     * 说明一下，为什么wait方法需要被synchronized修饰
     * 首先最直观的，不加锁会报错IllegalMonitorStateException
     * 其次，说明一下原理，多线程需要把对象锁住，那么通过什么方式锁呢，有且只有同步，不能volatile，不能Lock。
     * 再次，我们不止要将对象锁住，同时我们还要释放锁，那么释放锁完成时，我们修改的内容要对其他线程可见，这是后就必须用synchronized修饰
     * 最后，wait和notify已经notifyAll适用于线程之间通信也是如此，通过可见性，让数据其他线程可见，执行具有原子，以及根本不存在的重排序
     * 
     * 重排序具体请看案例一，每个线程获取到的时间片有可能打在前面
     * 
     */
    public static void validateWaitAndNotify() {
        Person person = new Person();
        new Thread(() -> {
            try {
                // 使用wait方法是必须要加synchronized否则不报java.lang.IllegalMonitorStateException
                synchronized (person) {
                    System.out.println("设置了姓名为JUC");
                    person.name = "JUC";
                    // 此处执行了等待阻塞，那么代码执行到此处就停止了
                    if (person.age == 0) {
                        System.out.println("爸爸被唤醒了");
                        person.notifyAll();
                        person.wait();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        
        //此处添加了sleep阻塞，看看能否被notify唤醒
        //并不能
        try {
            System.out.println(System.currentTimeMillis());
            Thread.sleep(5000);
            System.out.println(System.currentTimeMillis());
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        synchronized (person) {
            if (Objects.equals(person.name, "JUC")) {
                System.out.println(person.name);
            } else {
                try {
                    System.out.println("不符合爸爸的预期");
                    person.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            person.age = 18;
            person.notifyAll();
        }
        System.out.println(person);
    }
    
    /**
     * 实现并创建线程的三种方式
     * 
     * 需要注意的是，callable创建时需要用FutureTask进行适配，否则无法生成对应实现了的runnable接口的类
     */
    public static void createThread() {
        
        //采用Thread方法创建的线程
        new Thread(new ThreadMethodImpl(), "Thread-thread-1").start();
        
        //采用Runnable方法创建的线程
        new Thread(new RunnableMethodImpl(), "Thread-runnable-1").start();
        
        //采用Callable方法创建的线程
        new Thread(new FutureTask<String>(new CallableMethodImpl()), "Thread-callable-1").start();
    }
}

class Person {
    String name;
    int age = 0;

    @Override
    public String toString() {
        return "Person [name=" + name + ", age=" + age + "]";
    }

}

/**
 * 使用Thread方法实现自定义线程
 * 
 * @author yangcj
 *
 */
class ThreadMethodImpl extends Thread {

    @Override
    public void run() {
        System.out.println("我是Thread类继承的线程" + Thread.currentThread().getName());
        
        System.out.println("我在最后面等着你们1");
    }
}

/**
 * 使用Runnable方法实现自定义线程
 * 
 * @author yangcj
 *
 */
class RunnableMethodImpl implements Runnable {

    @Override
    public void run() {
        System.out.println("我是Runnable接口实现的线程" + Thread.currentThread().getName());
        
        System.out.println("我在最后面等着你们2");
    }

}

/**
 * 使用Callable实现自定义线程使用,JDK1.8才有
 * 
 * @author coddl
 *
 */
class CallableMethodImpl implements Callable<String> {

    @Override
    public String call() throws Exception {
        System.out.println("我是Callable接口实现的线程" + Thread.currentThread().getName());
        
        System.out.println("我在最后面等着你们3");
        return null;
    }

}