package com.squad13.apimonolito.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoAuditing
@EnableMongoRepositories(
        basePackages = "com.squad13.apimonolito.mongo",
        mongoTemplateRef = "mongoTemplate"
)
public class MongoConfig {
}
