package com.company.awms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AwmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AwmsApplication.class, args);


	}

}
