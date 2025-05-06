package com.kakaotech.ott.ott.like.domain.repository;

import com.kakaotech.ott.ott.like.infrastructure.entity.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;



public interface LikeJpaRepository extends JpaRepository<LikeEntity, Long> {


    boolean existsByUserEntityIdAndTargetId(Long userId, Long postId);

    void deleteByUserEntityIdAndTargetId(Long userId, Long postId);
}
