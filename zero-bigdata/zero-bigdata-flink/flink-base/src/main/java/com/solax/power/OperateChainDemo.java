package com.solax.power;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.util.Arrays;

public class OperateChainDemo {


    public static void main(String[] args) {
        StreamExecutionEnvironment environment = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());

        // 全家禁用算子链
        environment.disableOperatorChaining();

        // 全局设置并行度
        environment.setParallelism(3);

        //
        DataStreamSource<String> dataStreamSource = environment.socketTextStream("115.29.200.63", 8000);

        // lambda表达式关于泛型的类型擦除的问题
        // 本来通过正常的编写可以直接规定泛型的类型，进行预编译
        // 但是lambda不会识别泛型并且将泛型调整为object，形成了泛型的类型擦除，导致后续方法调用缺失
        // 取消算子链，本身不参与算子链合并
        dataStreamSource.disableChaining().flatMap((String value, Collector<String> collector) -> {
                    //用空格分隔为单词
                    String[] words = value.split(" ");
                    //统计单词使用频次，放入收集器
                    Arrays.stream(words)
                            //洗去前后空格
                            .map(String::trim)
                            //过滤掉空字符串
                            .filter(word -> !"".equals(word))
                            //加入收集器并发送给下游数据
                            .forEach(word -> collector.collect(word));
                }).returns(String.class)
                // 这意味着flatmap禁止使用算子链
                .disableChaining()
                .map(word -> new Tuple2<>(word, 1)).returns(Types.TUPLE(Types.STRING, Types.INT))

                .keyBy(0).sum(1).print();

        try {
            environment.execute("java test job");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
