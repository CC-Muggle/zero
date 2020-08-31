package com.sunfintech.virtual.machine.handle;

import java.util.ArrayList;
import java.util.List;

public class OutOfMemoryTest {

    /**
     * 测试堆溢出通过OutOfMemoryError把对应的日志详细信息dump出来
     * 
     * -Xms20M -Xmx20M  -XX:+HeapDumpOnOutOfMemoryError
     * 
     * @param args
     */
    public static void main(String[] args) {
        List<Object> list = new ArrayList<Object>();
        
        for (;;) {
            list.add(new Object());
        }
    }
}
