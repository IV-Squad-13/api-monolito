package com.squad13.apimonolito;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ApiMonolitoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiMonolitoApplication.class, args);
	}

}
