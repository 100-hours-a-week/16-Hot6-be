package com.kakaotech.ott.ott.recommendProduct.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.aiImage.domain.repository.AiImageJpaRepository;
import com.kakaotech.ott.ott.aiImage.infrastructure.entity.AiImageEntity;
import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.post.domain.model.Post;
import com.kakaotech.ott.ott.post.infrastructure.entity.PostEntity;
import com.kakaotech.ott.ott.recommendProduct.domain.model.AiImageRecommendedProduct;
import com.kakaotech.ott.ott.recommendProduct.domain.repository.AiImageRecommendedProductJpaRepsitory;
import com.kakaotech.ott.ott.recommendProduct.domain.repository.AiImageRecommendedProductRepository;
import com.kakaotech.ott.ott.recommendProduct.domain.repository.DeskProductJpaRepository;
import com.kakaotech.ott.ott.recommendProduct.infrastructure.entity.AiImageRecommendedProductEntity;
import com.kakaotech.ott.ott.recommendProduct.infrastructure.entity.DeskProductEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AiImageRecommendedProductRepositoryImpl implements AiImageRecommendedProductRepository {

    private final AiImageRecommendedProductJpaRepsitory aiImageRecommendedProductJpaRepsitory;
    private final AiImageJpaRepository aiImageJpaRepository;
    private final DeskProductJpaRepository deskProductJpaRepository;

    @Override
    public AiImageRecommendedProduct save(AiImageRecommendedProduct aiImageRecommendedProduct) {

        AiImageEntity aiImageEntity = aiImageJpaRepository.findById(aiImageRecommendedProduct.getAiImageId())
                .orElseThrow(() -> new CustomException(ErrorCode.AIIMAGE_NOT_FOUND));

        DeskProductEntity deskProductEntity = deskProductJpaRepository.findById(aiImageRecommendedProduct.getDeskProductId())
                .orElseThrow(() -> new CustomException(ErrorCode.DESK_PRODUCT_NOT_FOUND));

        AiImageRecommendedProductEntity aiImageRecommendedProductEntity = AiImageRecommendedProductEntity.from(aiImageRecommendedProduct, aiImageEntity, deskProductEntity);

        return aiImageRecommendedProductJpaRepsitory.save(aiImageRecommendedProductEntity).toDomain();
    }

    @Override
    public List<AiImageRecommendedProduct> findByAiImageIdAndDeskProductId(Long aiImageId, Long deskProductId) {

        return aiImageRecommendedProductJpaRepsitory.findByAiImageEntity_IdAndDeskProductEntity_id(aiImageId, deskProductId)
                .stream()
                .map(AiImageRecommendedProductEntity::toDomain)
                .toList();
    }

    @Override
    public List<AiImageRecommendedProduct> findByAiImageId(Long aiImageId) {

        return aiImageRecommendedProductJpaRepsitory.findByAiImageEntity_Id(aiImageId)
                .stream()
                .map(AiImageRecommendedProductEntity::toDomain)
                .toList();
    }


}
