package com.kakaotech.ott.ott.product.domain.repository;

import com.kakaotech.ott.ott.product.domain.model.DeskProduct;
import com.kakaotech.ott.ott.aiImage.infrastructure.entity.AiImageEntity;
import com.kakaotech.ott.ott.product.infrastructure.entity.ProductSubCategoryEntity;
import java.util.List;


public interface DeskProductRepository {

    DeskProduct save(DeskProduct deskProduct, ProductSubCategoryEntity productSubCategoryEntity, AiImageEntity aiImageEntity);

    List<DeskProduct> findByAiImageId(Long aiImageId);

    List<DeskProduct> findTop7ByWeight();

    void incrementScrapCount(Long postId, Long delta);
}
