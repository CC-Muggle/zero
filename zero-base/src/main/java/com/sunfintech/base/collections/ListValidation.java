package com.sunfintech.base.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * ArrayList与LinkedList区别
 *
 * 区别：
 *  1.ArrayList用数组实现，初始化默认大小10，每次扩容程度为原来的1.5倍
 *  2.LinkedList用链表实现，没有初始化大小且也不需要扩容
 *  3.虽然ArrayList和LinkedList都实现了List接口，但LinkedList还可以用于实现Queue集合
 *
 * 多线程应用：
 *  1.ArrayList和LinkedList皆为第二代集合，第一代集合为vector和hashTable之类的
 *  2.第二代List集合皆为线程不安全，因为大多数情况下不需要对该对象进行加锁，所以需要去除synchronize来提高性能
 *  3.可以通过Collections.synchronizedList实现第二代list的线程安全
 *  4.第三代List来自于JUC，采用了CAS原理执行非阻塞的锁，如CopyOnWriteArrayList
 *
 *
 * @author yangcj
 */
public class ListValidation {

    public static void main(String[] args) {
        createList();
    }

    private static void createList() {
        // 数组维护，第二集合ArrayList
        List<Integer> arrayList = new ArrayList<>();
        arrayList.add(123);
        arrayList.get(1);

        // 链表维护，第二集合LinkedList
        List<Integer> linkedList = new LinkedList<>();
        linkedList.add(123);
        linkedList.add(1);

        // 数组维护，第三集合CopyOnWriteArrayList，使用CAS来保证在线程共享的情况下正确修改
        List<Integer> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
        copyOnWriteArrayList.add(123);
        copyOnWriteArrayList.add(1);

    }
}
