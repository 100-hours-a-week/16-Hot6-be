package com.kakaotech.ott.ott.pointHistory.domain.repository;

import com.kakaotech.ott.ott.pointHistory.infrastructure.entity.PointHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PointHistoryJpaRepository extends JpaRepository<PointHistoryEntity, Long> {

    @Query("""
    SELECT DISTINCT p 
    FROM PointHistoryEntity p 
    LEFT JOIN FETCH p.userEntity u 
    WHERE u.id = :userId 
      AND (:lastPointHistoryId IS NULL OR p.id < :lastPointHistoryId) 
    ORDER BY p.id DESC
""")
    Page<PointHistoryEntity> findUserAllPointHistory(@Param("userId") Long userId, @Param("lastPointHistoryId") Long lastPointHistoryId, Pageable pageable);

    @Query("""
    SELECT p 
    FROM PointHistoryEntity p 
    WHERE p.userEntity.id = :userId 
    ORDER BY p.id DESC
    """)
    Optional<PointHistoryEntity> findLatestPointHistoryByUserId(@Param("userId") Long userId);

}
