package com.kakaotech.ott.ott.recommendProduct.domain.repository;

import com.kakaotech.ott.ott.post.infrastructure.entity.PostEntity;
import com.kakaotech.ott.ott.recommendProduct.infrastructure.entity.AiImageRecommendedProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AiImageRecommendedProductJpaRepsitory extends JpaRepository<AiImageRecommendedProductEntity, Long> {

    List<AiImageRecommendedProductEntity> findByAiImageEntity_IdAndDeskProductEntity_id(Long aiImageId, Long deskProductId);

    List<AiImageRecommendedProductEntity> findByAiImageEntity_Id(Long aiImageId);

}
