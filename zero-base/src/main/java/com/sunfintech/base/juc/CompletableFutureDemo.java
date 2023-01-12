package com.sunfintech.base.juc;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 测试CompletableFuture相关API内容
 *
 *
 */
public class CompletableFutureDemo {


    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 20, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

//        CompletableFuture<String> completableFuture1 = testApply(threadPoolExecutor);
//        CompletableFuture<Void> completableFuture2 = testAccept(threadPoolExecutor);
//        CompletableFuture<Void> completableFuture3 = testRun(threadPoolExecutor);
        testCombine(threadPoolExecutor);
        
        
        System.out.println("我是主线程，我先去忙别的了");
//        System.out.println("CompletableFuture返回内容："+ completableFuture1.join());
//        System.out.println("CompletableFuture返回内容："+ completableFuture2.join());
//        System.out.println("CompletableFuture返回内容："+ completableFuture3.join());

        threadPoolExecutor.shutdown();
    }

    /**
     * 测试CompletableFuture，结果型API
     * 
     * @param threadPoolExecutor
     * @return
     */
	public static CompletableFuture<String> testApply(ThreadPoolExecutor threadPoolExecutor) {
		CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("i come the first step");
            return "hello";
        }, threadPoolExecutor).thenApply(value -> {
            value = value + " chain";

            // thenApply报错会终止当前进度，后面的thenApply不再执行
//            int i = 10 / 0;

            System.out.println("into the second step");
            return value;
        }).thenApply(value -> {
            System.out.println("过程动画");
            return value;
        }).handle((value, exception) -> {
            // handle出现异常，下一个节点的handle也会执行，并带上上一个handle的异常结果，异常结果不能传递
             int i = 10/0;
            if (Objects.isNull(exception)) {
                System.out.println("finish you work, very well");
                return value + " call";
            } else {
                System.out.println("I got you, you has been arrested");
                return "I fucking failed";
            }
        }).handle((value, exception) -> {
            System.out.println("新的过场动画");
            return value;
        }).whenComplete((value, exception) -> {
        	// 执行完成
            if (exception == null) {
                System.out.println("I fucking did it");
            } else {
                System.out.println("exception appear");
            }
        }).exceptionally(exception -> {
            System.out.println(exception.getMessage());
            return null;
        });
		return completableFuture;
	}
	
	/**
	 * 测试CompletableFuture，消费型API
	 * @param threadPoolExecutor
	 * @return
	 */
	public static CompletableFuture<Void> testAccept(ThreadPoolExecutor threadPoolExecutor){
		CompletableFuture<Void> completableFuture = CompletableFuture.supplyAsync(() -> {
			// 逻辑业务处理
			return "fuck you;go get it";
		}, threadPoolExecutor).thenAccept(value -> {
			// 消费上一个动作的返回值，不进行任何响应
			System.out.println("直接消费线程执行结果：" + value);
		});
		return completableFuture;
	}
	
	/**
	 * 测试CompletableFuture，异步型API
	 * 
	 * @param threadPoolExecutor
	 * @return
	 */
	public static CompletableFuture<Void> testRun(ThreadPoolExecutor threadPoolExecutor){
		CompletableFuture<Void> completableFuture = CompletableFuture.supplyAsync(() -> {
			System.out.println("线程" + Thread.currentThread().getName() + "整活");
			return "I am done";
		}, threadPoolExecutor).thenRun(() -> {
			// 在上一个动作执行完成后，开启一个线程执行
			System.out.println("线程" + Thread.currentThread().getName() + "整活");
		}).thenRunAsync(() -> {
			// 在上一个动作执行完成后，重新开启一个线程池的内容去执行
			System.out.println("线程" + Thread.currentThread().getName() + "整活");
		});
		return completableFuture;
	}
	
	
	public static CompletableFuture<String> testCombine(ThreadPoolExecutor threadPoolExecutor){
		CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
			return "I am the storm ";
		}).thenCombine(CompletableFuture.supplyAsync(() -> {
			return "that it`s approaching";
		}), (value1, value2) -> {
			return value1 + value2;
		});
		
		// allof 方法就是为所有的completablefuture提供一个逻辑求和过程，例如：所有的线程全部执行完成，通过get来监听状态，若有线程未执行完成，则get处于阻塞状态
		CompletableFuture<Void> combineCompletableFuture = CompletableFuture.allOf(CompletableFuture.supplyAsync(() -> {
			return "povking,black cloud in islation";
		}), completableFuture);
		
		try {
			System.out.println(combineCompletableFuture.get());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return completableFuture;
		
	}
	
	
}
