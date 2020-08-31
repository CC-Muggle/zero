package com.sunfintech.base.grammar;

public class StringValidation {

    public static void main(String[] args) {
        String a = "hello";
        String b = ("he" + new String("llo"));
        
        System.out.println(a == b);
    }
}
