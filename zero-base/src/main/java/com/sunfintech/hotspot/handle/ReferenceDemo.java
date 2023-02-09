package com.sunfintech.hotspot.handle;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ReferenceDemo {

	public static void main(String[] args) {
//		testStrongeReference();
//		testSoftReference();
//		testWeakReference();
		
		testPhantomReference();
		
		
	}

	/**
	 * 启动参数：-Xms10m -Xmx10m
	 * 测试虚引用案例
	 * 
	 * 虚引用：
	 * 		1.本省不能获取到任何值，只会在被回收的时候网引用队列（ReferenceQueue）里添加值
	 * 		2.虚引用无时无刻会有可能会被回收，他只起到简单的通知作用
	 * 
	 */
	private static void testPhantomReference() {
		MyObject myObject = new MyObject();
		ReferenceQueue<MyObject> referenceQueue = new ReferenceQueue<MyObject>();
		PhantomReference<MyObject> phantomReference = new PhantomReference<MyObject>(myObject, referenceQueue);
		
		List<byte[]> list = new ArrayList<>();
		new Thread(() -> {
			while (true) {
				list.add(new byte[1 * 1024 * 1024]);
				System.out.println("队列新增成功，查看虚引用状态：" + phantomReference.get());
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}, "t1").start();
		
		new Thread(() -> {
			while (true) {
				Reference<? extends MyObject> object = referenceQueue.poll();
				if(object != null) {
					System.out.println("有东西加入了引用队列");
					break;
				}
			}
		}, "t2").start();
	}

	/**
	 * 
	 * 启动参数：无
	 * 测试弱引用案例
	 * 
	 * 弱引用：无论你内存是否足够，当发生GC的情况必须要回收
	 */
	private static void testWeakReference() {
		WeakReference<MyObject> weakReference = new WeakReference<MyObject>(new MyObject());
		System.out.println("当前物理内存够用：" + weakReference.get());
		
		System.gc();
		
		
		System.out.println("当前物理内存够用：" + weakReference.get());
	}

	/**
	 * 
	 * 启动参数：-Xms10m -Xmx10m
	 * 测试软引用案例
	 * 
	 * 软引用：当内存不够的时候触发GC才会被回收，内存充足的情况下不会回收
	 * 
	 */
	private static void testSoftReference() {
		SoftReference<MyObject> softReference = new SoftReference<MyObject>(new MyObject());
		
		System.gc();
		System.out.println("当前物理内存够用：" + softReference.get());
		
		try {
			byte[] bytes = new byte[20 * 1024 * 1024];
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("当前物理内存不够用了：" + softReference.get());
	}

	/**
	 * 
	 * 启动参数：无
	 * 测试强引用案例
	 * 
	 * 强引用：无论如何，只要不满足根可达算法，都会被回收，但是满足了就不会回收，甚至是出现了oom
	 * 
	 */
	private static void testStrongeReference() {
		MyObject myObject = new MyObject();
		System.out.println("创建了引用：" + myObject);
		
		myObject = null;
		// 这个代码没事别乱用，回答崩系统
		System.gc();
		System.out.println("显示销毁了引用：" + myObject);
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("是否真的调用了方法");
	}
}

class MyObject{
	
	/**
	 * 该方法只适用于虚拟机，一般我们不需要去复写也不可以去复写
	 */
	@Override
	public void finalize() {
		// TODO Auto-generated method stub
		System.out.println("我被回收了");
	}
}