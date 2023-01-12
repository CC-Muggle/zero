package com.sunfintech.base.juc;

import java.util.concurrent.locks.Lock;

import co.paralleluniverse.strands.concurrent.ReentrantLock;

/**
 * 验证Lock接口相关demo
 * 
 * 
 * 
 * @author yangcj
 *
 */
public class LockDemo {
	
	
	public static void main(String[] args) {
		Ticket ticket = new Ticket();
//		testFairLock(ticket);
//		testReentrantLock(ticket);
		
		
	}

	/**
	 * 
	 * 测试锁的可重入
	 * 
	 * @param ticket
	 */
	private static void testReentrantLock(Ticket ticket) {
		new Thread(() -> {
			synchronized (ticket) {
				System.out.println("我进来了");
				synchronized (ticket) {
					System.out.println("我又进来了");
					synchronized (ticket) {
						System.out.println("三进宫");
					}
				}
			}
		}).start();
		
		Lock lock = new ReentrantLock();
		new Thread(() -> {
			lock.lock();
			
			System.out.println(Thread.currentThread().getName() + "\t----------我进来了");
			
			lock.lock();
			System.out.println(Thread.currentThread().getName() + "\t----------我又进来了");
			
			lock.lock();
			System.out.println(Thread.currentThread().getName() + "\t----------三进宫");
			lock.unlock();
			lock.unlock();
			lock.unlock();
		}, "t1").start();
		
		new Thread(() -> {
			lock.lock();
			System.out.println(Thread.currentThread().getName() + "\t----------我进来了");
			lock.unlock();
		}, "t2").start();
	}

	/**
	 * 公平锁与非公平锁基本验证
	 * 
	 * 公平锁：公平锁在产生锁竞争了以后会进行排队
	 * 非公平锁：线程间发生竞争后，当前线程会与靠前的线程进行竞争，当竞争失败则进行排队，成功在获得锁
	 * 
	 * 为什么大部分锁默认都是非公平锁
	 * 
	 * 1.非公平锁是先去竞争锁再去排队的，那么对于CPU来说减少了线程挂起和唤醒的过程，减少了CPU的空闲时间
	 * 2.非公平锁一般情况下都是当前线程会得到锁，那么如此依赖就可以减少CPU线程间切换，从而提高性能
	 * 
	 * @param ticket
	 */
	private static void testFairLock(Ticket ticket) {
		new Thread(() -> {
			for (int i = 0; i < 50; i++) {
				ticket.sale();
			}
		}, "售票员A").start();
		new Thread(() -> {
			for (int i = 0; i < 50; i++) {
				ticket.sale();
			}
		}, "售票员B").start();
		new Thread(() -> {
			for (int i = 0; i < 50; i++) {
				ticket.sale();
			}
		}, "售票员C").start();
	}
	
	
}

class Ticket{
	
	private int number = 50;
	
	private int sale = 0;
	
	private Lock lock = new ReentrantLock(true);
	
	public void sale() {
		lock.lock();
		if(number != 0) {
			number = number - 1;
			sale++;
			System.out.println("当前" + Thread.currentThread().getName() + "卖出了第" + sale + "票，剩余：" + number + "张票");
		}
		lock.unlock();
	}
	
}