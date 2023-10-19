package com.solax.power;

import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class EnvironmentDemo {

    public static void main(String[] args) {
        // 创建一个自动识别的环境，如果拿到的是flink的环境（yarn集群或者standalone），否则自己创建一个standalone
        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();

        // 环境创建的参数设置
        Configuration configuration = new Configuration();
        configuration.set(RestOptions.PORT, 8081);

        // 自动情况下会自行进行转换(流批一体指定参数)
        // RuntimeExecutionMode.STREAMING、RuntimeExecutionMode.BATCH
        environment.setRuntimeMode(RuntimeExecutionMode.AUTOMATIC);

        // 创建一个本地环境，会根据当前环境配置自动去获取
//        StreamExecutionEnvironment environment = StreamExecutionEnvironment.createLocalEnvironment();

        // 创建一个远程环境，一般情况下针对于jar文件不在本地目录上的情况
//        StreamExecutionEnvironment environment = StreamExecutionEnvironment.createRemoteEnvironment("127.0.0.1", 8081, "/{jar包文件路径}");

        try {

            // 异步非阻塞执行算子
            environment.executeAsync();

            // 同步阻塞操作
            environment.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
        }
    }
}
