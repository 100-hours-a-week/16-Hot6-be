package com.kakaotech.ott.ott.product.application.service;

import java.io.IOException;
import java.util.List;

import com.kakaotech.ott.ott.product.domain.model.ProductType;
import com.kakaotech.ott.ott.product.domain.model.PromotionType;
import com.kakaotech.ott.ott.product.presentation.dto.response.ProductGetResponseDto;
import com.kakaotech.ott.ott.product.presentation.dto.response.ProductListResponseDto;
import com.kakaotech.ott.ott.recommendProduct.presentation.dto.response.ProductResponseDto;
import org.springframework.web.multipart.MultipartFile;

import com.kakaotech.ott.ott.product.presentation.dto.request.ProductCreateRequestDto;
import com.kakaotech.ott.ott.product.presentation.dto.response.ProductCreateResponseDto;

public interface ProductService {

    ProductCreateResponseDto createProduct(ProductCreateRequestDto productCreateRequestDto, Long userId) throws IOException;

    ProductGetResponseDto getProduct(Long productId, Long userId);

    ProductListResponseDto getProductList(
            Long userId,
            ProductType productType,
            PromotionType promotionType,
            Long lastProductId,
            int size);

    //    ProductCreateResponseDto updateProduct(Long productId, ProductCreateRequestDto request, List<MultipartFile> images);
    //    void deleteProduct(Long productId);

}