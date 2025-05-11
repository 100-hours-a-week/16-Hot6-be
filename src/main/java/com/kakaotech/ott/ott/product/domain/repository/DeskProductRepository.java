package com.kakaotech.ott.ott.product.domain.repository;

import com.kakaotech.ott.ott.product.domain.model.DeskProduct;
import com.kakaotech.ott.ott.aiImage.infrastructure.entity.AiImageEntity;
import com.kakaotech.ott.ott.product.infrastructure.entity.DeskProductEntity;
import com.kakaotech.ott.ott.product.infrastructure.entity.ProductSubCategoryEntity;

import java.util.List;


public interface DeskProductRepository {

    DeskProductEntity save(DeskProduct deskProduct, ProductSubCategoryEntity productSubCategoryEntity, AiImageEntity aiImageEntity);

    List<DeskProductEntity> findByAiImageId(Long aiImageId);
}
