package com.kakaotech.ott.ott.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")  // 모든 경로에 대해
                        .allowedOrigins("https://dev.onthe-top.com") // 허용할 origin
                        .allowedOrigins("https://dev-ai.onthe-top.com") // 허용할 origin
                        .allowedOrigins("https://dev-backend.onthe-top.com") // 허용할 origin
                        .allowedOrigins("http://localhost:3000")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS") // 허용할 메서드
                        .allowCredentials(true); // 쿠키 허용 시 true
            }
        };
    }
}