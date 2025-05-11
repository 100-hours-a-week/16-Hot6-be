package com.kakaotech.ott.ott.home.presentation.controller;

import com.kakaotech.ott.ott.global.response.ApiResponse;
import com.kakaotech.ott.ott.home.presentation.dto.response.MainResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<ApiResponse<MainResponseDto>> home() {



        MainResponseDto mainResponseDto = new MainResponseDto();

        return ResponseEntity.ok(ApiResponse.success("랜딩페이지 조회 성공", mainResponseDto));
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse> serverCheck() {

        return ResponseEntity.ok(ApiResponse.success("Server On", null));
    }

}