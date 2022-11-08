package com.sunfintech.base.juc;

import java.util.Objects;
import java.util.concurrent.*;

/**
 * 测试CompletableFuture相关API内容
 *
 *
 */
public class CompletableFutureDemo {


    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 20, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

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
            if (exception == null) {
                System.out.println("I fucking did it");
            } else {
                System.out.println("exception appear");
            }
        }).exceptionally(exception -> {
            System.out.println(exception.getMessage());
            return null;
        });


        System.out.println("我是主线程，我先去忙别的了");
        System.out.println("CompletableFuture返回内容："+completableFuture.join());

        threadPoolExecutor.shutdown();
    }
}
