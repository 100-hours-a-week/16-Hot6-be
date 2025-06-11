package com.kakaotech.ott.ott.recommendProduct.domain.repository;

import com.kakaotech.ott.ott.recommendProduct.domain.model.DeskProduct;
import com.kakaotech.ott.ott.aiImage.infrastructure.entity.AiImageEntity;
import com.kakaotech.ott.ott.recommendProduct.infrastructure.entity.ProductSubCategoryEntity;
import java.util.List;


public interface DeskProductRepository {

    DeskProduct save(DeskProduct deskProduct, ProductSubCategoryEntity productSubCategoryEntity, AiImageEntity aiImageEntity);

    List<DeskProduct> findByAiImageId(Long aiImageId);

    List<DeskProduct> findTop7ByWeight();

    void incrementScrapCount(Long postId, Long delta);

    DeskProduct findById(Long deskProductId);
}
