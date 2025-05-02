package com.kakaotech.ott.ott.aiImage.application.service;

import com.kakaotech.ott.ott.aiImage.domain.model.AiImage;
import com.kakaotech.ott.ott.aiImage.domain.model.DeskProduct;
import com.kakaotech.ott.ott.aiImage.domain.model.ProductMainCategory;
import com.kakaotech.ott.ott.aiImage.presentation.dto.request.AiImageAndProductRequestDto;

import java.util.List;

public interface ProductDomainService {

    List<DeskProduct> createdProduct(AiImageAndProductRequestDto aiImageAndProductRequestDto, AiImage aiImage, Long userId);
}
