package com.kakaotech.ott.ott.recommendProduct.domain.repository;

import com.kakaotech.ott.ott.recommendProduct.domain.model.AiImageRecommendedProduct;
import com.kakaotech.ott.ott.recommendProduct.presentation.dto.response.RecommendedProductProjection;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface AiImageRecommendedProductRepository {

    AiImageRecommendedProduct save(AiImageRecommendedProduct aiImageRecommendedProduct);

    List<AiImageRecommendedProduct> findByAiImageIdAndDeskProductId(Long aiImageId, Long deskProductId);

    List<AiImageRecommendedProduct> findByAiImageId(Long aiImageId);

    List<RecommendedProductProjection> findWithProductAndScrap(Long aiImageId, Long userId);
}
