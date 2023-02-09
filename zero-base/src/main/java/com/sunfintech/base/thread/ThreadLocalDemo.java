package com.sunfintech.base.thread;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * 测试ThreadLocal会保存每个关于当前线程的副本的操作
 * 
 * 
 * 
 * 
 * @author yangcj
 *
 */
public class ThreadLocalDemo {

	public static void main(String[] args) {
		
		testThreadLocal();
	}

	/**
	 * ThreadLocal顾名思义，线程本地内容，也就是每个线程都是独有的一个资源
	 * 
	 * ThreadLocal的使用是通过每个Thread类会自行维护一个ThreadLocal.ThreadLocalMap，这个Map就是一个键值对
	 * 通过当前线程作为基础获取这个线程维护的ThreadLocalMap，在以当前的ThreadLocal类作为对象的key去获取这个ThreadLocal对应得value值
	 * 
	 * 也就是说，一个线程可以通过多个ThreadLocal来维护自身里边的局部变量，这些变量是不需要写会内存中的，只需要在当前线程中使用
	 * 
	 * 关于ThreadLocal为什么要使用弱引用，因为ThreadLocal为了避免用户没有及时回收，导致内存泄漏，此时，弱引用会被GC强制回收
	 * 但有引发另一个问题，弱引用只是作为key存在，但是value还是强引用，所以我们需要使用remove把残留的value一起回收掉
	 * 
	 * 注意：ThreadLocal使用完成后必须remove掉，否则会导致当前线程被复用的情况，上一次处理的结果仍然保存在其中
	 * 
	 */
	private static void testThreadLocal() {
		House house = new House();
		CountDownLatch countDownLatch = new CountDownLatch(5);
		
		for (int i = 0; i < 5; i++) {
			new Thread(() -> {
				try {
					int random = new Random().nextInt(5) + 1;
					System.out.println(random);
					for (int j = 0; j < random; j++) {
						synchronized (house) {
							house.saleHouse();
						}
						house.saleVolume();
					}
					System.out.println(Thread.currentThread().getName() + "共出售：" + house.threadLocal.get() + "套房子");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					house.threadLocal.remove();
				}
				countDownLatch.countDown();
			}).start();
		}
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("共计卖出：" + house.saleCount);
	}
}

class House{
	
	int saleCount = 0;
	
	public void saleHouse() {
		saleCount++;
	}
	
	ThreadLocal<Integer> threadLocal = ThreadLocal.withInitial(() -> 0);
	
	public void saleVolume() {
		threadLocal.set(1 + threadLocal.get());
		
	}
}