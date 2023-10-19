package com.sunfintech.base;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Test {


    public static void main(String[] args) throws UnknownHostException {
        InetAddress address = InetAddress.getLocalHost();
        System.out.println(address.getHostAddress());
        System.out.println(address.getHostName());
    }
}
