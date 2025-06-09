package com.kakaotech.ott.ott.recommendProduct.application.service;

import com.kakaotech.ott.ott.aiImage.domain.model.AiImage;
import com.kakaotech.ott.ott.recommendProduct.domain.model.DeskProduct;
import com.kakaotech.ott.ott.aiImage.presentation.dto.request.AiImageAndProductRequestDto;
import com.kakaotech.ott.ott.recommendProduct.presentation.dto.response.RecommendedItemsDto;

import java.util.List;

public interface ProductDomainService {

    List<DeskProduct> createdProduct(AiImageAndProductRequestDto aiImageAndProductRequestDto, AiImage aiImage, Long userId);

    List<RecommendedItemsDto> getRecommendItems(Long userId);
}
