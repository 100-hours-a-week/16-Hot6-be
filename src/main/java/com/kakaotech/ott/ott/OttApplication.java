package com.kakaotech.ott.ott;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing	// AuditingEntityListener를 사용하기 위해 선언(시간 등록)
@EnableScheduling
@SpringBootApplication
public class OttApplication {

	public static void main(String[] args) {
		SpringApplication.run(OttApplication.class, args);
	}

}
