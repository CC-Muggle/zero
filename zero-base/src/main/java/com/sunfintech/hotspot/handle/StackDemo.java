package com.sunfintech.hotspot.handle;

public class StackDemo {

	public static void main(String[] args) {
		User customer = new User();
		handle(customer);
		System.out.println(customer);
	}
	
	public static void handle(User customer) {
		int i = 1;
		customer.setCount(i);
	}
}

class User {

	private int count;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "Customer [count=" + count + "]";
	}
	
}
