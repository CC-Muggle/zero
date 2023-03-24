package com.sunfintech.base.juc;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class ReadWriteLockDemo {

	private Map<String, String> map = new HashMap<String, String>();
	
	/**
	 * 非公平锁
	 */
	private Lock lock = new ReentrantLock();
	
	private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	
	public void write(String key, String value) {
		readWriteLock.writeLock().lock();
		try {
			System.out.println(Thread.currentThread() + "写入开始-----key:" + key + "\t value:" + value );
			map.put(key, value);
			TimeUnit.SECONDS.sleep(1); 
			System.out.println(Thread.currentThread() + "写入完成");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			readWriteLock.writeLock().unlock();
		}
	}
	
	public void read(String key) {
		readWriteLock.readLock().lock();
		try {
			System.out.println(Thread.currentThread() + "读取开始-----key:" + key);
			String result = map.get(key);
			TimeUnit.SECONDS.sleep(2); 
			System.out.println(Thread.currentThread() + "读取完成-----value:" + result);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			readWriteLock.readLock().unlock();
		}
	}
	
	public static void main(String[] args) {
		
		ReadWriteLockDemo demo = new ReadWriteLockDemo();
		for (int i = 0; i < 10; i++) {
			final int number = i;
			new Thread(() -> {
				demo.write("线程" + number, String.valueOf(number));
			}, "写线程" + i).start();
		}
		
		for (int i = 0; i < 10; i++) {
			final int number = i;
			new Thread(() -> {
				demo.read("线程" + number);
			}, "读线程" + i).start();
		}
		
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (int i = 0; i < 3; i++) {
			final int number = i;
			new Thread(() -> {
				demo.write("线程" + number, String.valueOf(number));
			}, "新写线程" + i).start();
		}
	}
}
