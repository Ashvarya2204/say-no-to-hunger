package com.saynotohunger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@EnableAsync
@SpringBootApplication
@EnableMethodSecurity
@EnableScheduling
@EnableJpaRepositories(basePackages = "com.saynotohunger.dao")
@EntityScan(basePackages = "com.saynotohunger.Entity")
public class SaynotohungerApplication 
{
	public static void main(String[] args) {
		SpringApplication.run(SaynotohungerApplication.class, args);
	}
}
