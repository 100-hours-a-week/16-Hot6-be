package com.kakaotech.ott.ott.scrap.domain.repository;

import com.kakaotech.ott.ott.scrap.domain.model.ScrapType;
import com.kakaotech.ott.ott.scrap.infrastructure.entity.ScrapEntity;
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
    @Query("SELECT s.targetId FROM ScrapEntity s WHERE s.userEntity.id = :userId AND s.targetId IN :postIds")
    List<Long> findScrappedPostIds(@Param("userId") Long userId, @Param("postIds") List<Long> postIds);

    @Query("SELECT COUNT(s) FROM ScrapEntity s WHERE s.post.id = :postId")
    int countByPostId(@Param("postId") Long postId);
}
