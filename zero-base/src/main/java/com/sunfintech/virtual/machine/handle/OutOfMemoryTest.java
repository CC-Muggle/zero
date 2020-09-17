package com.sunfintech.virtual.machine.handle;

import java.util.ArrayList;
import java.util.List;

/**
 * 验证堆内存溢出错误
 * 
 * 错误类型：java.lang.OutOfMemoryError: Java heap space
 * 出现原因：由于无尽的生产不可释放的对象，导致堆内存溢出
 * 
 * @author yangcj
 *
 */
public class OutOfMemoryTest {

    /**
     * 测试堆溢出通过OutOfMemoryError把对应的日志详细信息dump出来
     * 
     * 启动参数：
     * -Xms20M -Xmx20M  -XX:+HeapDumpOnOutOfMemoryError
     * 
     * @param args
     */
    public static void main(String[] args) {
        outOfHeapSpace();
    }

    /**
     * java.lang.OutOfMemoryError: Java heap space
     * 由于无尽的生产不可释放的对象，导致堆内存溢出
     */
    public static void outOfHeapSpace() {
        List<Object> list = new ArrayList<Object>();
        
        for (;;) {
            list.add(new Object());
        }
    }
}
