package com.kry.utility.servicepoller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class URLPollerApplication {

	public static void main(String[] args) {
		SpringApplication.run(URLPollerApplication.class, args);
	}

}
