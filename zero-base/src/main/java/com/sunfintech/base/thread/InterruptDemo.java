package com.sunfintech.base.thread;

/**
 * java线程的中断验证
 * 
 * java只提供了每条线程自己中断自己的方法，并没有中断别人的操作 中断只是一种协商机制，java中没有任何语法，中断是需要我们自己实现的
 * 
 * 当前线程调用其他线程的interrupted方法，其他线程并不是立即结束，而是讲中断位设置位true，剩下的有其他线程自行实现
 * 
 * @author yangcj
 *
 */
public class InterruptDemo {

	static volatile boolean interruptFlag = false;

	public static void main(String[] args) {
//		testInterrupt();

//		testInterruptException();

		testInterrupted();
	}

	/**
	 * interrupted：会返回当前线程的状态标记为，并且重置当前线程的中断标记
	 * 
	 * interrupt和interrupted区别 
	 * 1.只是interrupted方法为静态方法，interrupt方法为实例方法
	 * 2.interrupt方法不会重置中断标记位，interrupted方法会重置中断标记位
	 * 
	 */
	private static void testInterrupted() {
		System.out.println(Thread.currentThread().getName() + ":" + Thread.interrupted());
		System.out.println(Thread.currentThread().getName() + ":" + Thread.interrupted());
		System.out.println("------------------------1");
		Thread.currentThread().interrupt();
		System.out.println("------------------------2");
		System.out.println(Thread.currentThread().getName() + ":" + Thread.interrupted());
		System.out.println(Thread.currentThread().getName() + ":" + Thread.interrupted());
	}

	/**
	 * 
	 * 1.初始情况下，中断标志位统一都是false 2.当一个线程处于正常活动状态的情况下，会将该线程的终端标志位设置位true
	 * 3.当一个线程处于阻塞状态（sleep，wait，join），那么线程会立刻解除阻塞，清除当前线程中断标记，并抛出一个InterruptedException
	 * 4.当线程执行结束后，中断不会产生任何影响
	 * 
	 */
	private static void testInterruptException() {
		Thread t1 = new Thread(() -> {
			while (true) {
				if (Thread.currentThread().isInterrupted()) {
					System.out.println(Thread.currentThread().getName() + "中断标志位："
							+ Thread.currentThread().isInterrupted() + "程序结束");
					break;
				}
				System.out.println(Thread.currentThread().getName() + "运行中");
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// 若产生中，则需要继续中断，因为sleep，wait，join等方法会让中断标记复位
					Thread.currentThread().interrupt();
					e.printStackTrace();
				}
			}
		}, "t1");
		t1.start();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		new Thread(() -> t1.interrupt()).start();
	}

	/**
	 * 测试中断
	 * 
	 * 1.interrupt：设置中断标志位，但不会影响线程本身运行状态，需要自己去判断当前线程标记
	 * 2.isInterrupted：实例方法、会返回当前线程的中断标记位，但是不会重置当前线程的标记为，除非当前线程为不活跃的或已结束的线程
	 * 
	 */
	private static void testInterrupt() {
		Thread t1 = new Thread(() -> {
			while (true) {
				if (Thread.currentThread().isInterrupted()) {
					System.out.println(Thread.currentThread().getName() + "线程被打断了");
					break;
				}
				System.out.println(Thread.currentThread().getName() + "线程循环中");
			}
			System.out.println("线程" + Thread.currentThread().getName() + "已结束");
		}, "t1");
		t1.start();

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Thread t2 = new Thread(() -> {
			t1.interrupt();
			System.out.println(Thread.currentThread().getName() + "线程尝试打断别人");
		}, "t2");
		t2.start();
	}
}
