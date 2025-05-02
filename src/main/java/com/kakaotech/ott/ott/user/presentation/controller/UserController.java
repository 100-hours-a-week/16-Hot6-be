package com.kakaotech.ott.ott.user.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController("/api/v1")
@RequiredArgsConstructor
public class UserController {

    @GetMapping("/auth/{provider}")
    public ResponseEntity<Void> login(@PathVariable String provider) {

        String redirectUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/oauth2/authorization/")
                .path(provider)
                .build()
                .toUriString();

        return ResponseEntity.status(302)
                .header("Location", redirectUrl)
                .build();
    }
}
