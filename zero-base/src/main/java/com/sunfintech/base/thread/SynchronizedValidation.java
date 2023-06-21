package com.sunfintech.base.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 验证synchronized关键字的作用
 * 
 * synchronized同步锁，又称对象锁也就是被wait释放的锁，没有竞争就没有阻塞 总结：线程访问资源类
 * 
 * synchronized为什么可以为所有的对象上锁： 因为每个对象本省会带有一个ObjectMonitor的一个对象，他就是这个对象本身的锁
 * monitor中会记录该对象一些头相关信息，例如，锁，wait集合，重入（同一线程在持有锁的情况下反复获取锁）次数
 * 
 * 1.修饰类型的范围 2.双重锁校验demo 3.生产者消费者模型
 * 
 * @author yangcj
 *
 */
public class SynchronizedValidation {

	public static void main(String[] args) {
		testConsumerProductor();
	}

	/**
	 * 验证synchronized的作用域
	 */
	private static void testSynchronized() {
		for (int i = 0; i < 10; i++) {
			new Thread(() -> {
				Student student = Student.getInstance();
				System.out.println("小伙汁的名字是" + student.getName() + "，你的班级名称是什么呀" + student.getClassroom());
			}, "Thread_" + i).start();
		}
	}

	/**
	 * 验证生产者消费者模型
	 * 
	 * 当生产者生产了5个商品之后，消费者去消费所有商品，否则去唤醒生产者线程
	 * 
	 * wait是阻塞自己当前线程，并释放对象锁
	 * wait为什么需要配合synchronized一起使用，是因为对象锁中有一个object monitor，object monitor中会记录锁的相关信息和其他相关的线程以便于唤醒
	 *     而synchronized是获取这个object monitor的一个关键字，notify同理
	 *     
	 * notify非当前线程外的其他线程，若存在多个其他线程则会随机唤醒一个，notifyAll则是全部唤醒
	 * 
	 * 生产者消费者模型是为了解决两端效率不一致从而导致的信息不对称的解决方案，因为锁会偏向当前持有的线程，会导致生产了过剩或者消费能力过剩
	 * 那么，为了不浪费cpu，我们会阻塞某一条精力过剩的线程，转而吧锁指向其他线程，从而保证消费能力和生产能力的平衡
	 * 
	 * 
	 */
	private static void testConsumerProductor() {

		List<String> list = new ArrayList<String>();

		// 生产者线程
		new Thread(() -> {
			int i = 0;
			while (true) {
				synchronized (list) {
					if (list.size() <= 5) {
						list.add("我是第" + i + "个商品");
						System.out.println(Thread.currentThread().getName() + "生产商品：" + i);
						i++;
						// 当生产者生产的商品大于5个之后，提醒消费者去消费商品，并阻塞自己不在生产
						if(list.size() > 5) {
							list.notify();
						}
					} else {
						// 当队列中元素小于5个时，新增商品，否则进入等待状态
						try {
							list.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}
			}
		}, "productor").start();

		// 消费者线程
		new Thread(() -> {
			synchronized (list) {
				while (true) {
					if (list.size() > 0) {
						String str = list.get(0);
						list.remove(str);
						System.out.println(Thread.currentThread().getName() + "获得商品：" + str);
						
						// 当消费者消费的商品小于0个之后，提醒生产者去生产商品，并阻塞自己不在消费
						if(list.size() == 0) {
							list.notify();
						}
					} else {
						// 当队列中元素大于0个时，消耗商品，否则进入等待状态
						try {
							list.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}, "consumer").start();

	}

}

class Student {

	String name;

	String classroom;

	AtomicInteger age;

	static String type = "human";

	static String school = "奔波儿灞";

	private volatile static Student student;

	private Student() {

	}

	/**
	 * synchronized实现双重锁校验
	 * 
	 * 这里要说明一下对象锁释放情况，这种情况创建单例在对其设置初始值是非常危险，此类是针对于不需要谁知初始值或
	 * 初始值已经配置好的情况下亦或者初始值的设置在实例方法且是同步方法的情况下完成，否则会出现同步块内有初始值 未设置然后就被其他线程获取了
	 * 
	 * 修正一下synchronize概念，对synchronized而言，参与了竞争才会被阻塞，但是不参与竞争的还是没有被阻塞
	 * 当被其他线程运行阻塞了以后（不会释放对象锁），此时就会非常危险，虽然执行顺序有保证，但是原子性就没法保证了
	 * 
	 * 说下为什么添加volatile关键字，由于synchronized只对竞争锁进行了单线程操作，也就是说在块内还是会进行重排序的
	 * 那么，由于该情况下其他线程可能不参与锁竞争，所以一旦重排序就可能产生new对象时有了引用还没有对引用生成对应的资源 引起其他线程使用的时候找不到资源对象。
	 * 
	 * 这里提一下，new操作会现在虚拟机分配引用在分配内存使用，所以整个操作不需要保证有序，可能导致先后倒置
	 * 
	 * 
	 */
	public static Student getInstance() {
		if (Objects.isNull(student)) {
			synchronized (Student.class) {
				if (Objects.isNull(student)) {
					student = new Student();
					student.name = Thread.currentThread().getName();
					System.out.println("当先线程的名字是个啥" + student.name);

//                    try {
//                        Thread.sleep(3000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }

					student.classroom = Thread.currentThread().getName();
					System.out.println("----------------------synchronize这玩意有重排序吗-----------------------------，当前线程："
							+ Thread.currentThread().getName());
				}
			}
		}
		return student;
	}

	/**
	 * synchronized能够修饰静态方法
	 * 
	 * 对静态方法加上同步关键字时相当于对整个类进行加锁 此时的synchronized不叫做对象，因为锁定的对象是类本身
	 * 
	 * @return
	 */
	public static synchronized String getType() {
		return Student.type;
	}

	public static String getSchool() {
		String school = null;
		synchronized (Student.class) {
			school = Student.school;
		}
		return school;
	}

	/**
	 * synchronized能够修饰对象方法
	 * 
	 * 对对象进行加锁，在操作对象时只有一个线程能操作，且被修饰的整个过程必须是原子的 此时的synchronized可以称之为对象
	 * 
	 * @return
	 */
	public synchronized String getName() {
		return this.name;
	}

	public String getClassroom() {
		String classroom = null;
		synchronized (this) {
			classroom = this.classroom;
		}
		return classroom;
	}
}