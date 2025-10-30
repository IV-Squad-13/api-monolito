package com.squad13.apimonolito.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.squad13.apimonolito.repository")
public class JpaConfig {
}