package com.kakaotech.ott.ott.recommendProduct.domain.repository;

import com.kakaotech.ott.ott.recommendProduct.domain.model.AiImageRecommendedProduct;

import java.util.List;

public interface AiImageRecommendedProductRepository {

    AiImageRecommendedProduct save(AiImageRecommendedProduct aiImageRecommendedProduct);

    List<AiImageRecommendedProduct> findByAiImageIdAndDeskProductId(Long aiImageId, Long deskProductId);

    List<AiImageRecommendedProduct> findByAiImageId(Long aiImageId);
}
