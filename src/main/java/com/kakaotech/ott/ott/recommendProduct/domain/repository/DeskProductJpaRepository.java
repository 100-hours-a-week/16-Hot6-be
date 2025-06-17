package com.kakaotech.ott.ott.recommendProduct.domain.repository;

import com.kakaotech.ott.ott.recommendProduct.infrastructure.entity.DeskProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DeskProductJpaRepository extends JpaRepository<DeskProductEntity, Long> {

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

    @Query("SELECT DISTINCT d FROM DeskProductEntity d " +
            "WHERE (:lastWeight IS NULL OR d.weight < :lastWeight " +
            "OR (d.weight = :lastWeight AND d.id < :lastDeskPostId)) " +
            "ORDER BY d.weight DESC, d.id DESC")
    Page<DeskProductEntity> findAllDeskProductsByWeight(@Param("lastWeight") Double lastWeight,
                                        @Param("lastDeskPostId") Long lastDeskPostId,
                                        Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "UPDATE desk_products d " +
            "SET d.weight = (d.scrap_count * 0.5) + (d.click_count * 0.8)",
            nativeQuery = true)
    void batchUpdateWeights();

    @Modifying(clearAutomatically = true)
    @Query("""
    UPDATE DeskProductEntity d 
    SET d.clickCount = GREATEST(0, d.clickCount + :delta)
    WHERE d.id = :deskProductId
""")
    void incrementClickCount(@Param("deskProductId") Long deskProductId, @Param("delta") Long delta);

}
