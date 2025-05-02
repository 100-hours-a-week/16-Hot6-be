package com.kakaotech.ott.ott.user.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/v1")
@RequiredArgsConstructor
public class UserController {

    @PostMapping("/auth/{provider}")
    public ResponseEntity<Void> login(@PathVariable String provider) {

        String redirectUrl = "http://localhost:8080/oauth2/authorization/" + provider;
        return ResponseEntity.status(302)
                .header("Location", redirectUrl)
                .build();
    }
}
