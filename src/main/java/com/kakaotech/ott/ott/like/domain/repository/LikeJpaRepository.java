package com.kakaotech.ott.ott.like.domain.repository;

import com.kakaotech.ott.ott.like.infrastructure.entity.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface LikeJpaRepository extends JpaRepository<LikeEntity, Long> {


    boolean existsByUserEntityIdAndTargetId(Long userId, Long postId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM LikeEntity l WHERE l.userEntity.id = :userId AND l.targetId = :postId")
    void deleteByUserEntityIdAndTargetId(Long userId, Long postId);

    @Query("SELECT COUNT(l) FROM LikeEntity l WHERE l.targetId = :postId")
    int countByPostId(@Param("postId") Long postId);
}
