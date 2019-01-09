package com.adc.mq.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean(name = "pg")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.postgresql")
    public DataSource pgDataSource() {
        return DataSourceBuilder.create().build();
    }
}
