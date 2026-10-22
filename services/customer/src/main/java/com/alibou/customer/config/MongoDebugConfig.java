package com.alibou.customer.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.mongodb.autoconfigure.MongoConnectionDetails;
import org.springframework.boot.mongodb.autoconfigure.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoDebugConfig {

    @Bean
    CommandLineRunner mongoPropertiesDebug(
            MongoProperties properties,
            MongoConnectionDetails connectionDetails) {

        return args -> {
            System.out.println("========== MONGO PROPERTIES ==========");
            System.out.println("URI      : " + properties.getUri());
            System.out.println("Host     : " + properties.getHost());
            System.out.println("Port     : " + properties.getPort());
            System.out.println("Database : " + properties.getDatabase());
            System.out.println("Username : " + properties.getUsername());

            System.out.println("========== MONGO CONNECTION DETAILS ==========");
            System.out.println("Connection String : " + connectionDetails.getConnectionString());

            System.out.println("==============================================");
        };
    }
}