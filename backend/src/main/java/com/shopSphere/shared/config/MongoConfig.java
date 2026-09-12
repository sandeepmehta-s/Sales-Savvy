package com.shopSphere.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;

@Configuration
public class MongoConfig {

    @Bean
    public MongoTransactionManager transactionManager(@org.springframework.lang.NonNull MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }
}
