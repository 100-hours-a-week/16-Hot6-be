package com.kakaotech.ott.ott.product.domain.repository;

import com.kakaotech.ott.ott.product.infrastructure.entity.DeskProductEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DeskProductJpaRepository extends JpaRepository<DeskProductEntity, Long> {

    List<DeskProductEntity> findByAiImageEntity_Id(Long aiImageId);

    // JPA에서 Pageable로 Top N 조회 (정렬 + 제한)
    @Query("SELECT d FROM DeskProductEntity d ORDER BY d.weight DESC")
    List<DeskProductEntity> findByOrderByWeightDesc(Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("""
    UPDATE DeskProductEntity d 
    SET d.scrapCount = GREATEST(0, d.scrapCount + :delta)
    WHERE d.id = :id
    """)
    void incrementScrapCount(@Param("id") Long id, @Param("delta") Long delta);
}
