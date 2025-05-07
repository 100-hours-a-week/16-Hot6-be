package com.kakaotech.ott.ott.scrap.domain.repository;

import com.kakaotech.ott.ott.scrap.infrastructure.entity.ScrapEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ScrapJpaRepository extends JpaRepository<ScrapEntity, Long> {

    boolean existsByUserEntityIdAndTargetId(Long userId, Long postId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ScrapEntity s WHERE s.userEntity.id = :userId AND s.targetId = :postId")
    void deleteByUserEntityIdAndTargetId(Long userId, Long postId);
}
