package com.sunfintech.base.juc;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 
 * FutureTask 分别实现了Runnable，Future<>接口，得以变成一个异步任务
 * FutureTask中的Callable成员变量实现了能从中获取返回值
 * 
 * 综上所述，FutureTask是一个异步线程实现类
 * 
 */
public class FutureTaskDemo {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
//		testFutureTask();

		testCompletableFuture();
	}

	/**
	 * 
	 * CompletableFuture不推荐使用构造方法直接获得，而是采用runAsync和supplyAsync获取实例
	 * 原因：由于其中会初始化某些线程池，在直接new的情况下会获取到一个不完整的CompletableFuture实例，所以不推荐大家使用
	 * 
	 * 1.简单的创建异步线程（如果是new获取的实例，则需要自己启动该线程）
	 * 2.通过runAsync和supplyAsync启动线程，并执行相关内容
	 * 3.可以通过whenComplete对线程中执行完成的结果进行加以处理
	 * 4.可以通过exceptionally捕获异常对异常情况加以处理
	 * 
	 * 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private static void testCompletableFuture() throws InterruptedException, ExecutionException {
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
			System.out.println(Thread.currentThread().getName());
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			int result = ThreadLocalRandom.current().nextInt(10);
			System.out.println("获取到的随机数：" + result);
			if (result > 5) {
				int i = 10 / 0;
			}
			return result;
		}, executorService).whenComplete((value, exception) -> {
			// 没有异常的情况
			if (Objects.isNull(exception)) {
				System.out.println("更新数据库value:" + value);
			}
		}).exceptionally(exception -> {
			System.out.println("出现异常，回滚单元exception：" + exception.getStackTrace());
			return 0;
		});

		System.out.println(Thread.currentThread().getName() + "线程先去忙别的了");
		executorService.shutdown();
	}

	/**
	 * future不能解决的痛点： 1.future在获取结果的情况下会阻塞当前线程，从而导致CPU资源浪费 2.通过轮询获取结果虽然不会导致阻塞，但是不够优雅
	 * 
	 * 3.无法进行回调，造成轮询情况下的变相阻塞 4.
	 */
	private static void testFutureTask() {
		FutureTask<String> futureTask = new FutureTask<String>(() -> {
			System.out.println("我进入了callable接口内部了");
			return "我被调用了";
		});
		new Thread(futureTask).start();

		try {
			Thread.sleep(3000);

			while (true) {
				if (futureTask.isDone()) {
					// 在使用get方法时，会存在阻塞当前线程
					System.out.println("拿来吧你");
					String result = futureTask.get();
					System.out.println(result);
					break;
				} else {
					System.out.println("你特么好了没有");
				}
			}
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("主线程结束");
	}
}
