package com.example.springsecurity;

import com.example.springsecurity.Model.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringsecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringsecurityApplication.class, args);
//		User user=User.builder().
//				id(23)
//				.name("musthak")
//				.email("musthak@gmail.com")
//				.password("123")
//				.build();
	}

}
