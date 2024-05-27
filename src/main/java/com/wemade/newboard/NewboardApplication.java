package com.wemade.newboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class NewboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewboardApplication.class, args);
	}

}
