package com.sunfintech.base.thread;

/**
 * 守护线程
 * 
 * 
 * 
 * @author yangcj
 *
 */
public class DeamonThreadDemo {
	
	
	
	public static void main(String[] args) {
		testDeamon();
	}

	/**
	 * 
	 * 守护线程必须要在thread开始之前设置，否则会报java.lang.IllegalThreadStateException
	 * 并且是非守护线程
	 * 
	 * @param args
	 */
	private static void testDeamon() {
		// 开启常规线程，当调用setDaemon时，会将该线程标记为守护线程
		Thread t1 = new Thread(() -> {
			System.out.println(Thread.currentThread().isDaemon() ? "守护线程" : "用户线程");
			while(true) {
				
			}
		});
		t1.start();
		t1.setDaemon(true);
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("主线程已关闭");
	}
}
