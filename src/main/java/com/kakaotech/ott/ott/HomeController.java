package com.kakaotech.ott.ott;

import com.kakaotech.ott.ott.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Welcome to OnTheTop Backend!";
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse> serverCheck() {

        return ResponseEntity.ok(ApiResponse.success("Server On", null));
    }
}