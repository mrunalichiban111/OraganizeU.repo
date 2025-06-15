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

		System.out.println("🔍 DB_URL = " + env.getProperty("DB_URL"));
		System.out.println("🔍 DB_USERNAME = " + env.getProperty("DB_USERNAME"));
		System.out.println("🔍 DB_PASSWORD = " + env.getProperty("DB_PASSWORD"));
	}

}

