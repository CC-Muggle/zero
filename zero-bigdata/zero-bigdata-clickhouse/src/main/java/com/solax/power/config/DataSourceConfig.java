package com.solax.power.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    /**
     * 用druid去管理clickhouse的连接池
     *
     * 注意：druid必须要1.1.7以上的版本
     * @param config
     * @return
     */
    @Bean
    public DataSource getDataSource(ClickHouseConfig config){
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(config.getUrl());
        dataSource.setPassword(config.getPassword());
        dataSource.setDriverClassName(config.getDriverClassName());
        dataSource.setInitialSize(config.getInitialSize());
        dataSource.setMinIdle(config.getMinIdle());
        dataSource.setMaxActive(config.getMaxActive());
        dataSource.setMaxWait(config.getMaxWait());
        return dataSource;
    }

}
