package com.kakaotech.ott.ott.recommendProduct.presentation.controller;

import com.kakaotech.ott.ott.global.response.ApiResponse;
import com.kakaotech.ott.ott.recommendProduct.application.service.DeskProductService;
import com.kakaotech.ott.ott.recommendProduct.presentation.dto.response.DeskProductDetailResponseDto;
import com.kakaotech.ott.ott.recommendProduct.presentation.dto.response.DeskProductListResponseDto;
import com.kakaotech.ott.ott.user.domain.model.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/desk-products")
@RequiredArgsConstructor
public class DeskProductController {

    private final DeskProductService deskProductService;

    @GetMapping
    public ResponseEntity<ApiResponse<DeskProductListResponseDto>> getRecommendedProducts(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(required = false) Long lastDeskProductId,
            @RequestParam(required = false) Double lastWeight,
            @RequestParam(defaultValue = "10") int size) {

        Long userId = (userPrincipal == null) ? null : userPrincipal.getId();

        DeskProductListResponseDto deskProductListResponseDto = deskProductService.getDeskProducts(userId, lastWeight, lastDeskProductId, size);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("추천 제품 리스트 조회 성공", deskProductListResponseDto));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<DeskProductDetailResponseDto>> getRecommendedProduct(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long productId) {

        Long userId = (userPrincipal == null) ? null : userPrincipal.getId();

        DeskProductDetailResponseDto deskProductDetailResponseDto = deskProductService.getDeskProduct(userId, productId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("추천 제품 조회 성공", deskProductDetailResponseDto));
    }
}
