package com.kakaotech.ott.ott.product.domain.repository;

import com.kakaotech.ott.ott.product.domain.model.ProductType;
import com.kakaotech.ott.ott.product.domain.model.PromotionType;
import com.kakaotech.ott.ott.product.presentation.dto.response.ProductListResponseDto;

public interface ProductVariantQueryRepository {

    ProductListResponseDto findProductListByCursor(Long userId, ProductType productType, PromotionType promotionType,
            Long lastVariantId, int size);
}
