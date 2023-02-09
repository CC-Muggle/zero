package com.sunfintech.base.juc;

import java.util.concurrent.locks.LockSupport;

/**
 * 
 * LockSupport锁支撑
 * 
 * 
 * @author yangcj
 *
 */
public class LockSupportDemo {

	public static void main(String[] args) {
		testLockSupport();
	}

	/**
	 * 
	 * 锁支撑是什么：
	 * 一个静态方法模式来解决线程阻塞和唤醒的超简洁用法，每个线程在LockSupport只会有一个通行证（permit）
	 * 1.不需要持有锁才能进行的wait和notify
	 * 2.可以不需要在意wait和notify的顺序，是以通行证的模式来解决一个线程阻塞的时代码的先后顺序
	 * 
	 * 注意：一个线程的通行证只有一个，不能累计，那么，LockSupport必须是一对一的模式，但绝对不能使用多个锁
	 * 
	 * 
	 */
	private static void testLockSupport() {
		Thread t1 = new Thread(() -> {
			System.out.println(Thread.currentThread().getName() + "----------我进来了");
			LockSupport.park();
			System.out.println(Thread.currentThread().getName() + "----------被唤醒");
		});
		t1.start();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		new Thread(() -> {
			System.out.println(Thread.currentThread().getName() + "----------发出通知");
		}, "t2").start();
	}
}
