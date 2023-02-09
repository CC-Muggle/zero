package com.sunfintech.base.juc;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;

/**
 * 验证CAS(compare and swap)原理的实际应用
 * 
 * 1.原子基础类型的应用
 * 2.什么CAS,CAS能干什么
 * 3.ABA问题是怎么产生的，怎么解决的呢
 * 4.原子引用，版本号标记解决ABA问题
 * 5.unsafe类的探索
 * 
 * @author yangcj
 *
 */
public class AtomicValidation {
	
	
	private AtomicReference<Thread> atomicReference = new AtomicReference<Thread>();
	
	
	public volatile int money;
	
	AtomicIntegerFieldUpdater<AtomicValidation> fieldUpdater = AtomicIntegerFieldUpdater.newUpdater(AtomicValidation.class, "money");
	
	public void lock() {
		Thread thread = Thread.currentThread();
		while (!atomicReference.compareAndSet(null, thread)) {
			
		};
		System.out.println(thread.getName() + "加锁成功");
	}
	
	public void unlock() {
		Thread thread = Thread.currentThread();
		atomicReference.compareAndSet(thread, null);
		System.out.println(thread.getName() + "解锁成功");
	}
	
	public Integer addMoney() {
		return fieldUpdater.addAndGet(this, 1000);
	}
	
	

    public static void main(String[] args) {
    	testLongAccumulator();
    	
    }

    /**
     * 
     * LongAdder、LongAccumulator
     * 这两个类增强了原子累加的应用，在get和sum的情况下是不准确的结果，但是对于大量的数据统计是不必须的
     * 这两个类对于并发操作优于AtomicLong，主要原因是因为通过分散热点的方式提高了并发量
     * 通过cells新建扩容点
     * 
     */
	private static void testLongAccumulator() {
		LongAdder longAdder = new LongAdder();
    	for (int i = 0; i < 50; i++) {
			new Thread(() -> {
				longAdder.increment();
			}).start();
		}
    	System.out.println("原子累加结果：" + longAdder.sum());
    	
    	
    	LongAccumulator longAccumulator = new LongAccumulator((x,y) -> x + y, 0);
    	for (int i = 1; i <= 50; i++) {
    		final long number = i;
    		new Thread(() -> {
    			longAccumulator.accumulate(number);
			}).start();
		}
    	System.out.println("原子计算器结果：" + longAccumulator.get());
	}

    /**
     * 粒度更细的原子字段锁
     */
	private static void testFieldUpdater() {
		AtomicValidation atomicValidation = new AtomicValidation();
    	
    	CountDownLatch countDownLatch = new CountDownLatch(10);
    	for (int i = 0; i < 10; i++) {
    		new Thread(() -> {
    			for (int j = 0; j < 10; j++) {
    				System.out.println(atomicValidation.addMoney());;
    				
				}
    			countDownLatch.countDown();
    		}).start();
		}
    	
    	try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	System.out.println(atomicValidation.money);
	}

    /**
     * 自旋锁
     * 
     * 自旋锁核心思想就是CAS（compare and swap）
     * 通过unsafe类里边的方法，实现比较并交换。
     * unsafe类是一个不安全的，可以直接操作物理内存的一个类。
     * 
     */
	private static void testSpinLock() {
		AtomicValidation atomicValidation = new AtomicValidation();
    	
    	new Thread(() -> {
    		atomicValidation.lock();
    		System.out.println("--------------- 操作中");
    		
    		try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    		
    		atomicValidation.unlock();
    	}, "A").start();
    	
    	try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
    	new Thread(() -> {
    		atomicValidation.lock();
    		
    		atomicValidation.unlock();
    	}, "B").start();
	}

    /**
     * ABA问题的出现和解决
     * 这里注意一下，启动的该demo的时候一定要添加-XX:AutoBoxCacheMax=2048此参数，否则不能使用Integer作为原子引用
     * 
     * 问题的起源是在于一个线程A修改一个数据时间很长，另一个线程B修改了数据但之后又把数据修改回原来的样子，此时线程A针对于期望值是没有改变的，那么就能够修改成功
     * 而解决方案是参照乐观锁的控制原理，使用版本号或者时间戳来绑定数据修改的次数，使得每一次数据修改都有自己的版本
     * 
     */
    public static void validationABASolution() {
        // 会触发ABA问题
        AtomicInteger atomicInteger = new AtomicInteger(135);
        // 有效的通过版本号来避免ABA问题
        AtomicStampedReference<Integer> stampedInteger = new AtomicStampedReference<Integer>(135, 1);
        
        new Thread(()->{
            System.out.println("线程" + Thread.currentThread().getName()+ "当前atomicInteger的值为" + atomicInteger.get());
            System.out.println("线程" + Thread.currentThread().getName()+ "当前stampedInteger的值为" + stampedInteger.getReference() + "当前版本号" + stampedInteger.getStamp());
            //等待3s，是的两条线程打印的atomicInteger的值一致
            try {
                TimeUnit.SECONDS.sleep(3L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            System.out.println("线程" + Thread.currentThread().getName()+ "修改当前atomicInteger结果" + atomicInteger.compareAndSet(135, 1588) + "值的结果为" + atomicInteger.get());
            System.out.println("线程" + Thread.currentThread().getName()+ "修改当前atomicInteger结果" + atomicInteger.compareAndSet(1588, 135) + "值的结果为" + atomicInteger.get());
            
            System.out.println("线程" + Thread.currentThread().getName()+ "修改当前stampedInteger结果" + stampedInteger.compareAndSet(135, 1588, 1, 2) + "值的结果为" + stampedInteger.getReference() + "当前版本号" + stampedInteger.getStamp());
            System.out.println("线程" + Thread.currentThread().getName()+ "修改当前stampedInteger结果" + stampedInteger.compareAndSet(1588, 135, 2, 3) + "值的结果为" + stampedInteger.getReference() + "当前版本号" + stampedInteger.getStamp());
        }, "Thread-A").start();
        
        new Thread(()->{
            System.out.println("线程" + Thread.currentThread().getName()+ "当前atomicInteger的值为" + atomicInteger.get());
            System.out.println("线程" + Thread.currentThread().getName()+ "当前stampedInteger的值为" + stampedInteger.getReference() + "当前版本号" + stampedInteger.getStamp());
            //等待7s，为了触发ABA问题所需时间
            try {
                TimeUnit.SECONDS.sleep(7L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("线程" + Thread.currentThread().getName()+ "修改当前atomicInteger结果" + atomicInteger.compareAndSet(135, 1670) + "值的结果为" + atomicInteger.get());
            
            System.out.println("线程" + Thread.currentThread().getName()+ "修改当前stampedInteger结果" + stampedInteger.compareAndSet(135, 1670, 1, 2) + "值的结果为" + stampedInteger.getReference() + "当前版本号" + stampedInteger.getStamp());
            
        },"Thread-B").start();
    }

    /**
     * 以下是基础的原子基本类型，这些类的操作都是原子的，经常配合volatile使用
     * 
     * 什么是CAS呢，也就是下面的compareAndswap，比较并设置。
     * CAS会有让用户程序员填写如一个期望值，当期望值与当期值相符，那么就可以改变当前值为想设置的值。若不满足期望值，则修改失败
     * CAS能够帮助用户程序员完成对一个值的非阻塞修改，减少系统开销。
     * 
     * 原子类内部都是通过unsafe类进行维护
     * unsafe类是来自于JDK的rt.jar(运行时包也叫runtime包)，unsafe类通过操作CPU原语来实现原子操作
     * compareAndSwapInt是来自于AtomicInteger的compareAndSet()方法，通过传入传入对象本地，地址，期望值，更新值来使用
     * 
     * 
     */
    public static void baseAtomicObject() {
        AtomicInteger atomicInteger = new AtomicInteger(135);
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        AtomicLong atomicLong = new AtomicLong(13335L);
        
        System.out.println("CAS比较后" + atomicInteger.compareAndSet(135, 225) + "设置后可得结果atomicInteger：" + atomicInteger.get());
        System.out.println("CAS比较后" + atomicBoolean.compareAndSet(false, true) + "设置后可得结果atomicBoolean：" + atomicBoolean.get());
        System.out.println("CAS比较后" + atomicLong.compareAndSet(13555L, 1670L) + "设置后可得结果atomicLong：" + atomicLong.get());
    }
}
