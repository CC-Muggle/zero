package com.sunfintech.base.juc;

import java.util.concurrent.locks.Lock;

import co.paralleluniverse.strands.concurrent.ReentrantLock;

/**
 * 验证Lock接口相关demo
 * 
 * 
 * 使用Lock接口完成一个死锁
 * 
 * 
 * 
 * @author yangcj
 *
 */
public class LockDemo {
	
	
	private volatile int i = 0;

	public static void main(String[] args) {
		Ticket ticket = new Ticket();
//		testFairLock(ticket);
//		testReentrantLock(ticket);
		testDeadLock();

	}

	/**
	 * 死锁如何触发
	 * 
	 * 死锁必须一个线程的执行过程必须要持有两把以上的锁才会触发
	 * 1.线程1持有锁1，线程2持有锁2，那么在同一时刻线程一去获取锁2，线程2去获取锁1则会形成死锁
	 * 2.避免死锁首先就是要减小加锁的粒度，指定到某个尽可能小的范围去避免一个执行过程获取两把以上的锁
	 * 3.减少对象锁的嵌套使用，当一个线程持有一个对象锁，那么就尽可能减少在持有锁的前提下去获取其他对象锁
	 * 
	 * 死锁如何排查
	 * 
	 * jps -l获取可能发生死锁的java进程
	 * 
	 * jstack 进程号，查看java进程中是否发现deadlock
	 * 
	 * 如果使用的Lock相关接口造成的死锁很难排查，因为Lock的实现原理是通过CAS进行锁定的，持有锁的线程会一直处于运行中的状态，不会出现deadlock
	 * 
	 * 
	 */
	private static void testDeadLock() {
		Lock lock1 = new ReentrantLock();
		Lock lock2 = new ReentrantLock();
		
		LockDemo lockDemo = new LockDemo();
		new Thread(() -> {
			lock1.lock();
			System.out.println(Thread.currentThread().getName() + "lock1我进来了");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			lock2.lock();
			lockDemo.i = lockDemo.i - 1;
			System.out.println(Thread.currentThread().getName() + "lock2我进来了");
			lock2.unlock();
			lock1.unlock();
		}, "t1").start();

		new Thread(() -> {
			lock2.lock();
			System.out.println(Thread.currentThread().getName() + "lock2我进来了");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			lock1.lock();
			System.out.println(Thread.currentThread().getName() + "lock1我进来了");
			lockDemo.i = lockDemo.i + 1;
			lock1.unlock();
			lock2.unlock();
		}, "t2").start();
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
	 * 公平锁：公平锁在产生锁竞争了以后会进行排队 非公平锁：线程间发生竞争后，当前线程会与靠前的线程进行竞争，当竞争失败则进行排队，成功在获得锁
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

class Ticket {

	private int number = 50;

	private int sale = 0;

	private Lock lock = new ReentrantLock(true);

	public void sale() {
		lock.lock();
		if (number != 0) {
			number = number - 1;
			sale++;
			System.out.println("当前" + Thread.currentThread().getName() + "卖出了第" + sale + "票，剩余：" + number + "张票");
		}
		lock.unlock();
	}

}