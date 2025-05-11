package com.kakaotech.ott.ott.aiImage.domain.repository;

import com.kakaotech.ott.ott.aiImage.infrastructure.entity.AiImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AiImageJpaRepository extends JpaRepository<AiImageEntity, Long> {

    Optional<AiImageEntity> findByBeforeImagePath(String beforeImagePath);

    // Batch 조회 쿼리
    @Query("SELECT a FROM AiImageEntity a WHERE a.postId IN :postIds")
    List<AiImageEntity> findByPostIdIn(@Param("postIds") List<Long> postIds);

}
