package com.portfolio.backend.infra.data;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class TransactionConfiguration {
    
    @Bean
    public PlatformTransactionManager transaction(DataSource ds) {
        return new DataSourceTransactionManager(ds);
    }
}
