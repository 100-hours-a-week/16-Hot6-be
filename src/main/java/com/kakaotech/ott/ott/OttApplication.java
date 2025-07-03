package com.kakaotech.ott.ott;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@EnableScheduling
@SpringBootApplication
public class OttApplication {

	public static void main(String[] args) {
		SpringApplication.run(OttApplication.class, args);
	}

}
