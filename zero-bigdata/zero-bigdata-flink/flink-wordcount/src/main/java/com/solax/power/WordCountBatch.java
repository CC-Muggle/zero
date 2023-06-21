package com.solax.power;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.AggregateOperator;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.FlatMapOperator;
import org.apache.flink.api.java.operators.UnsortedGrouping;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

import java.util.Arrays;

/**
 * 批处理案例（已过时）
 *
 *
 */
public class WordCountBatch {

    public void execute() {
        ExecutionEnvironment environment = ExecutionEnvironment.getExecutionEnvironment();

        // 从resource文件夹下（Classpath）中获取
        String filePath = getClass().getClassLoader().getResource("batch.txt").getFile();
        // 流处理执行模式
        // 获取数据流（有界）
        DataSource<String> dataStreamSource = environment.readTextFile(filePath);

        //wordcount计算, Tuple2指的是二元组数据
        FlatMapOperator<String, Tuple2<String, Integer>> wordCountTuple2 = dataStreamSource.flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>() {
            /**
             * map计算
             * @param value 输入数据 用空格分隔的句子
             * @param out map计算之后的收集器
             * @throws Exception
             */
            @Override
            public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {
                //用空格分隔为单词
                String[] words = value.split(" ");
                //统计单词使用频次，放入收集器
                Arrays.stream(words)
                        //洗去前后空格
                        .map(String::trim)
                        //过滤掉空字符串
                        .filter(word -> !"".equals(word))
                        //加入收集器
                        .forEach(word -> out.collect(new Tuple2<>(word, 1)));
            }
        });
        // groupBy的入参0是指代的二元组的第0个元素
        UnsortedGrouping<Tuple2<String, Integer>> tuple2UnsortedGrouping = wordCountTuple2.groupBy(0);
        AggregateOperator<Tuple2<String, Integer>> sum = tuple2UnsortedGrouping.sum(1);
        try {
            sum.print();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
