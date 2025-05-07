package com.kakaotech.ott.ott.aiImage.domain.repository;

import com.kakaotech.ott.ott.aiImage.domain.model.DeskProduct;
import com.kakaotech.ott.ott.aiImage.infrastructure.entity.AiImageEntity;
import com.kakaotech.ott.ott.aiImage.infrastructure.entity.DeskProductEntity;
import com.kakaotech.ott.ott.aiImage.infrastructure.entity.ProductSubCategoryEntity;

import java.util.List;


public interface DeskProductRepository {

    DeskProductEntity save(DeskProduct deskProduct, ProductSubCategoryEntity productSubCategoryEntity, AiImageEntity aiImageEntity);

    List<DeskProductEntity> findByAiImageId(Long aiImageId);
}
