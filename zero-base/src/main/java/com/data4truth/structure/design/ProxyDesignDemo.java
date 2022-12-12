package com.data4truth.structure.design;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 代理模式的实现方式
 * <p>
 * JDK代理
 * cglib代理
 *
 * @author yangcj
 */
public class ProxyDesignDemo {

    public static class TargetHandler implements ProxyHandlerInterface {

        @Override
        public void print() {
            System.out.println("目标对象被调用了当前方法");
        }
    }


    public static void main(String[] args) {
//        proxyJDK();
    }

    private static void proxyJDK() {
        // 各种各样的手段获取到目标对象
        TargetHandler target = new TargetHandler();

        // 创建代理对象，并实现相关代理内容
        ProxyHandlerInterface proxy = (ProxyHandlerInterface) Proxy.newProxyInstance(ProxyDesignDemo.class.getClassLoader(), new Class[]{ProxyHandlerInterface.class},
                (object, method, params) -> {
                    // method 对应目标对象的实际方法
                    System.out.println("代理对象被调用");
                    return method.invoke(target, params);
                });

        // 启动代理对象调用
        proxy.print();
    }
}

/**
 * 代理接口
 */
interface ProxyHandlerInterface {

    /**
     * 被代理类的方法
     */
    void print();
}
