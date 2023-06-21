package com.solax.power;

public class App {

    public static void main(String[] args) {
//        WordCountBatch process = new WordCountBatch();
//        WordCountDataStream process = new WordCountDataStream();
        WordCountDataStreamUnbound process = new WordCountDataStreamUnbound();
        process.execute();

    }

}
