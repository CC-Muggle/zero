package com.sunfintech.hotspot.handle;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.openjdk.jol.info.ClassLayout;


/**
 * 针对一个对象在JVM中的详细解答
 * 
 * 一个对象分为：对象头（Header），实例数据（Instance Data），对齐单元（Padding）
 * 
 * 对象头：对象头分为两部分存储，分别是对象标记（Mark Word）和类型指针（Class Pointer）。在64bit系统中这两部分各站8个字节，共计16字节
 * 		对象标记MarkWord：这里边会保存对象的HashCode，GC次数，GC标记，同步锁标记，偏向锁标记
 * 						对象标记会随着使用的过程来复用自己的存储空间，是个非固定大小的数据结构，会随着锁的变化而变化。
 * 		类型指针ClassPointor：这里边会保存对于当前对象的类源信息，指向的是保存在方法区中的类信息
 * 
 * 实例数据：存放了对象的字段信息，同时也包括父类的相关信息。
 * 		
 * 对齐填充：对齐填充是是当前实例化的对象占用的内存为8的倍数，当不足8的倍数的空间时，就会使用对齐填充去将对象大小补足到8的倍数字节上。因为虚拟机要求
 * 			一个对象的开头地址必须得是8的整数倍，所以需要对齐填充补充一个对象所占用的字节数
 * 
 * 
 * 添加启动参数：
 * 
 * @author yangcj
 *
 */
public class ObjectDemo {

	public static void main(String[] args) {
		Object object = new Object();
		
		// hashcode 不是一个对象出生就有的，他是被调用了hashcode方法之后才会出现的
//		System.out.println("10禁止hashcode：" + object.hashCode());
//		
//		System.out.println("2禁止hashcode：" + Integer.toBinaryString(object.hashCode()));
//		
//		System.out.println(ClassLayout.parseInstance(object).toPrintable());
		
		
		
		Customer customer = new Customer();
		CountDownLatch countDownLatch = new CountDownLatch(10);
		// 禁止转偏向锁
		System.out.println("禁止偏向锁，hashcode：" + customer.hashCode());
		for (int i = 0; i < 10; i++) {
			new Thread(() -> {
				synchronized (customer) {
					System.out.println(Thread.currentThread().getName() + "当前线程执行过程-------------------------" );
					System.out.println("16禁止hashcode：" + Integer.toHexString(object.hashCode()));
					System.out.println(ClassLayout.parseInstance(customer).toPrintable());
					countDownLatch.countDown();
				}
			}, "t" + i).start();
		}
		
		
		// countdown等待所有线程执行完毕
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println(Thread.currentThread().getName() + "当前线程执行过程-------------------------" );
		System.out.println(ClassLayout.parseInstance(customer).toPrintable());
		
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(Thread.currentThread().getName() + "当前线程执行过程-------------------------" );
		System.out.println(ClassLayout.parseInstance(customer).toPrintable());
	}
}

class Customer{
	
	int id;
	boolean flag;
	String ibm;
	
	
}
