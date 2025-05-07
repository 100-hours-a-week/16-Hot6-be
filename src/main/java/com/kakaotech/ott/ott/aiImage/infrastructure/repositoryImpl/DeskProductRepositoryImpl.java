package com.kakaotech.ott.ott.aiImage.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.aiImage.domain.model.DeskProduct;
import com.kakaotech.ott.ott.aiImage.domain.repository.DeskProductJpaRepository;
import com.kakaotech.ott.ott.aiImage.domain.repository.DeskProductRepository;
import com.kakaotech.ott.ott.aiImage.infrastructure.entity.AiImageEntity;
import com.kakaotech.ott.ott.aiImage.infrastructure.entity.DeskProductEntity;
import com.kakaotech.ott.ott.aiImage.infrastructure.entity.ProductSubCategoryEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DeskProductRepositoryImpl implements DeskProductRepository {

    private final DeskProductJpaRepository deskProductJpaRepository;

    @Override
    public DeskProductEntity save(DeskProduct deskProduct, ProductSubCategoryEntity productSubCategoryEntity,
                                  AiImageEntity aiImageEntity) {

        return deskProductJpaRepository.save(DeskProductEntity.from(deskProduct, productSubCategoryEntity, aiImageEntity));
    }

    @Override
    public List<DeskProductEntity> findByAiImageId(Long aiImageId) {
        return deskProductJpaRepository.findByAiImageEntity_Id(aiImageId);
    }
}
