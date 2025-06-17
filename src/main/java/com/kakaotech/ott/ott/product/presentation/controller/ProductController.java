package com.kakaotech.ott.ott.product.presentation.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.product.application.service.ProductService;
import com.kakaotech.ott.ott.product.domain.model.ProductType;
import com.kakaotech.ott.ott.product.domain.model.PromotionType;
import com.kakaotech.ott.ott.product.presentation.dto.request.VariantDto;
import com.kakaotech.ott.ott.product.presentation.dto.response.ProductGetResponseDto;
import com.kakaotech.ott.ott.product.presentation.dto.response.ProductListResponseDto;
import com.kakaotech.ott.ott.user.domain.model.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.web.multipart.MultipartHttpServletRequest;

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
            HttpServletRequest request) throws IOException {
            
        Long userId = userPrincipal.getId();
        // 각 Variant별 이미지 추출
        Map<Integer, List<MultipartFile>> variantImagesMap = extractVariantImages(request, variants.size());

        validateBasicImagePresence(variantImagesMap);
        ProductCreateRequestDto productCreateRequestDto = ProductCreateRequestDto.builder()
                .product(productInfo)
                .variants(variants)
                .variantImagesMap(variantImagesMap)
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

    @GetMapping
    public ResponseEntity<ApiResponse<ProductListResponseDto>> getProductList(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(required = false) ProductType productType,
            @RequestParam(required = false) PromotionType promotionType,
            @RequestParam(required = false) Long lastProductId,
            @RequestParam(defaultValue = "10") int size) {

        Long userId = (userPrincipal == null) ? null : userPrincipal.getId();

        // 입력 검증
        if (size > 50) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        // 특가 타입과 상품 타입은 동시에 사용할 수 없음
        if (promotionType != null && productType != null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        ProductListResponseDto productList = productService.getProductList(
                userId, productType, promotionType, lastProductId, size);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("상품 목록 조회 성공", productList));
    }


    // === Private Methods ===
    // 각 Variant별 이미지 추출 메서드
    private Map<Integer, List<MultipartFile>> extractVariantImages(HttpServletRequest request, int variantCount) {
        Map<Integer, List<MultipartFile>> variantImagesMap = new HashMap<>();

        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

            for (int i = 0; i < variantCount; i++) {
                String paramName = "variant_" + i + "_images";
                List<MultipartFile> images = multipartRequest.getFiles(paramName);

                // 빈 리스트로 초기화 (null 방지)
                variantImagesMap.put(i, images != null ? images : new ArrayList<>());
            }

        } catch (Exception e) {
            log.error("이미지 추출 중 오류 발생", e);
            throw new CustomException(ErrorCode.INVALID_INPUT_CODE);
        }

        return variantImagesMap;
    }

    private void validateBasicImagePresence(Map<Integer, List<MultipartFile>> imageMap) {
        for (List<MultipartFile> images : imageMap.values()) {
            if (images == null || images.stream().allMatch(MultipartFile::isEmpty)) {
                throw new CustomException(ErrorCode.VARIANT_IMAGE_REQUIRED);
            }
        }
    }
}