package com.projects.api_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class ApiServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiServiceApplication.class, args);
	}

	@PostConstruct
	public void debug() {
		System.out.println("DB_HOST=" + System.getenv("DB_HOST"));
		System.out.println("SPRING_URL=" + System.getProperty("spring.datasource.url"));
	}	
}
