package com.kakaotech.ott.ott.recommendProduct.domain.repository;

import com.kakaotech.ott.ott.recommendProduct.infrastructure.entity.AiImageRecommendedProductEntity;
import com.kakaotech.ott.ott.recommendProduct.presentation.dto.response.RecommendedProductProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AiImageRecommendedProductJpaRepsitory extends JpaRepository<AiImageRecommendedProductEntity, Long> {

    List<AiImageRecommendedProductEntity> findByAiImageEntity_IdAndDeskProductEntity_id(Long aiImageId, Long deskProductId);

    List<AiImageRecommendedProductEntity> findByAiImageEntity_Id(Long aiImageId);

    @Query("""
    SELECT 
        dp.id AS productId,
        dp.name AS productName,
        dp.imagePath AS imagePath,
        dp.price AS price,
        dp.purchaseUrl AS purchaseUrl,
        CASE WHEN s.id IS NOT NULL THEN true ELSE false END AS isScrapped,
        arp.centerX AS centerX,
        arp.centerY AS centerY,
        dp.weight AS weight
    FROM AiImageRecommendedProductEntity arp
    JOIN arp.deskProductEntity dp
    LEFT JOIN ScrapEntity s ON s.userEntity.id = :userId AND s.type = 'PRODUCT' AND s.targetId = dp.id
    WHERE arp.aiImageEntity.id = :aiImageId
""")
    List<RecommendedProductProjection> findWithProductAndScrap(@Param("aiImageId") Long aiImageId,
                                                               @Param("userId") Long userId);

}
