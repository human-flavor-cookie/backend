package com.cookie.human_flavor_cookie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HumanFlavorCookieApplication {

	public static void main(String[] args) {
		SpringApplication.run(HumanFlavorCookieApplication.class, args);
	}

}
