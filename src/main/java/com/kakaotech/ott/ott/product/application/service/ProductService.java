package com.kakaotech.ott.ott.product.application.service;

import java.io.IOException;
import java.util.List;

import com.kakaotech.ott.ott.product.presentation.dto.response.ProductGetResponseDto;
import com.kakaotech.ott.ott.recommendProduct.presentation.dto.response.ProductResponseDto;
import org.springframework.web.multipart.MultipartFile;

import com.kakaotech.ott.ott.product.presentation.dto.request.ProductCreateRequestDto;
import com.kakaotech.ott.ott.product.presentation.dto.response.ProductCreateResponseDto;

public interface ProductService {

    ProductCreateResponseDto createProduct(ProductCreateRequestDto productCreateRequestDto, Long userId) throws IOException;


//    ProductCreateResponseDto updateProduct(Long productId, ProductCreateRequestDto request, List<MultipartFile> images);
//
//
//    void deleteProduct(Long productId);


    ProductGetResponseDto getProduct(Long productId, Long userId);


//    ProductListResponseDto getProductList(String type, Long cursorId, int size);
}