package com.kakaotech.ott.ott.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/public/hello")
    public String publicHello() {
        return "Hello, Public!";
    }

    @GetMapping("/api/private/hello")
    public String privateHello() {
        return "Hello, Authenticated User!";
    }
}
