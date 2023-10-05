package com.linkzilla;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class LinkzillaApplication {

	public static void main(String[] args) {
		SpringApplication.run(LinkzillaApplication.class, args);
	}

}
