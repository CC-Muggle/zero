package com.sunfintech.base.lambda;

import java.util.ArrayList;

public class LambdaDemo {

    public static void main(String[] args) {
        System.out.println(new ArrayList<String>().stream().findFirst().orElse(null));
    }
}
