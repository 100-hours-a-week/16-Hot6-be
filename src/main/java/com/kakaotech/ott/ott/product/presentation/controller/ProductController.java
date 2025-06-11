package com.kakaotech.ott.ott.product.presentation.controller;

import java.io.IOException;
import java.util.List;

import com.kakaotech.ott.ott.product.application.service.ProductService;
import com.kakaotech.ott.ott.product.presentation.dto.request.VariantDto;
import com.kakaotech.ott.ott.product.presentation.dto.response.ProductGetResponseDto;
import com.kakaotech.ott.ott.user.domain.model.UserPrincipal;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.kakaotech.ott.ott.global.response.ApiResponse;
import com.kakaotech.ott.ott.product.presentation.dto.request.ProductCreateRequestDto;
import com.kakaotech.ott.ott.product.presentation.dto.response.ProductCreateResponseDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Validated
public class ProductController {

    private final ProductService productService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProductCreateResponseDto>> createProduct(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestPart("product") @Valid ProductCreateRequestDto.ProductInfo productInfo,
            @RequestPart("variants") @Valid List<VariantDto> variants,
            @RequestPart("images") @Valid @Size(max=5) List<MultipartFile> images) throws IOException {
            
        Long userId = userPrincipal.getId();
        ProductCreateRequestDto productCreateRequestDto = ProductCreateRequestDto.builder()
                .product(productInfo)
                .variants(variants)
                .images(images)
                .build();
        ProductCreateResponseDto productCreateResponseDto = productService.createProduct(productCreateRequestDto, userId);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("상품 등록 완료", productCreateResponseDto));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductGetResponseDto>> getProductDetail(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long productId) {
        Long userId = (userPrincipal == null) ? null : userPrincipal.getId();

        ProductGetResponseDto productDetail = productService.getProduct(productId, userId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("상품 상세 정보 조회 성공", productDetail));
    }
}