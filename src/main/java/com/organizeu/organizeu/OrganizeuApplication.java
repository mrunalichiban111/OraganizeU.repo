package com.organizeu.organizeu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class OrganizeuApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(OrganizeuApplication.class, args);
		Environment env = context.getEnvironment();

		System.out.println("🔍 spring.datasource.url = " + env.getProperty("spring.datasource.url"));
		System.out.println("🔍 spring.datasource.username = " + env.getProperty("spring.datasource.username"));
		System.out.println("🔍 spring.datasource.password = " + env.getProperty("spring.datasource.password"));
	}

}

