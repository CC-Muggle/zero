package com.solax.power;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.util.Arrays;

/**
 * 流式处理
 * 读文件操作，有界流处理
 *
 */
public class WordCountDataStream {

    public void execute() {
        // 获取flink的环境
        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();

        // 从resource文件夹下（Classpath）中获取
        String filePath = getClass().getClassLoader().getResource("batch.txt").getFile();
        DataStreamSource<String> dataStreamSource = environment.readTextFile(filePath);

        SingleOutputStreamOperator<Tuple2<String, Integer>> tuple2Collect = dataStreamSource.flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>() {
                    @Override
                    public void flatMap(String value, Collector<Tuple2<String, Integer>> collector) throws Exception {
                        //用空格分隔为单词
                        String[] words = value.split(" ");
                        //统计单词使用频次，放入收集器
                        Arrays.stream(words)
                                //洗去前后空格
                                .map(String::trim)
                                //过滤掉空字符串
                                .filter(word -> !"".equals(word))
                                //加入收集器并发送给下游数据
                                .forEach(word -> collector.collect(new Tuple2<>(word, 1)));
                    }
                })
                // 这里直接就提供了相关的分组与求和
                // 与DataSet一样，keyBy等同于groupBy，入参0指代的是Tuple2二元组的第0个元素
                .keyBy(0).sum(1);
        tuple2Collect.print();
        try {
            // 执行操作，在DataStream模式下是必须要调用的
            environment.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
