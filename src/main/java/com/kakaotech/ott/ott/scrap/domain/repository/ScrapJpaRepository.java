package com.kakaotech.ott.ott.scrap.domain.repository;

import com.kakaotech.ott.ott.scrap.domain.model.ScrapType;
import com.kakaotech.ott.ott.scrap.infrastructure.entity.ScrapEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScrapJpaRepository extends JpaRepository<ScrapEntity, Long> {

    boolean existsByUserEntityIdAndTypeAndTargetId(Long userId, ScrapType type, Long targetId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ScrapEntity s WHERE s.userEntity.id = :userId AND s.targetId = :postId")
    void deleteByUserEntityIdAndTargetId(Long userId, Long postId);

    // Batch 조회 쿼리
    @Query("SELECT s.targetId FROM ScrapEntity s WHERE s.userEntity.id = :userId AND s.type = 'POST' AND s.targetId IN :postIds")
    List<Long> findScrappedPostIds(@Param("userId") Long userId, @Param("postIds") List<Long> postIds);


    @Query("SELECT s.targetId FROM ScrapEntity s WHERE s.userEntity.id = :userId AND s.type = 'SERVICE_PRODUCT' AND s.targetId IN :productIds")
    List<Long> findScrappedServiceProductIds(@Param("userId") Long userId, @Param("productIds") List<Long> productIds);

    @Query("SELECT COUNT(s) FROM ScrapEntity s WHERE s.targetId = :postId AND s.type = :type")
    int countByPostId(@Param("postId") Long postId);

    // 나의 스크랩 조회
    @Query("""
    SELECT DISTINCT s 
    FROM ScrapEntity s 
    LEFT JOIN FETCH s.userEntity u 
    WHERE u.id = :userId 
      AND (:lastScrapId IS NULL OR s.id < :lastScrapId) 
    ORDER BY s.id DESC
""")
    Page<ScrapEntity> findUserAllScraps(@Param("userId") Long userId, @Param("lastScrapId") Long lastPostId, Pageable pageable);
}
