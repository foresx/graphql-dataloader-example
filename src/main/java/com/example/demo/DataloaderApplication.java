package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class})
@EnableJpaRepositories
@EntityScan
@EnableTransactionManagement
public class DataloaderApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataloaderApplication.class, args);
	}

}
