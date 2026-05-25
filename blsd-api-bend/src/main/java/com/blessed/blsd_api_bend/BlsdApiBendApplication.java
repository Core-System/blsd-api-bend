package com.blessed.blsd_api_bend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BlsdApiBendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlsdApiBendApplication.class, args);
	}

}