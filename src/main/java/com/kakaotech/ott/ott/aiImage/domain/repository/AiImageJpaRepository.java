package com.kakaotech.ott.ott.aiImage.domain.repository;

import com.kakaotech.ott.ott.aiImage.infrastructure.entity.AiImageEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AiImageJpaRepository extends JpaRepository<AiImageEntity, Long> {

    Optional<AiImageEntity> findByBeforeImagePath(String beforeImagePath);

    @Query("""
        SELECT ai 
        FROM AiImageEntity ai 
        WHERE ai.userEntity.id = :userId AND ai.state = 'SUCCESS'
        AND ai.id < :cursorId
        ORDER BY ai.id DESC
    """)
    Slice<AiImageEntity> findByUserWithCursor(
            @Param("userId") Long userId,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    // Batch 조회 쿼리
    @Query("SELECT a FROM AiImageEntity a WHERE a.postId IN :postIds")
    List<AiImageEntity> findByPostIdIn(@Param("postIds") List<Long> postIds);

    @Query("SELECT a FROM AiImageEntity a WHERE a.postId = :postId")
    Optional<AiImageEntity> findByPostId(@Param("postId") Long postId);

}
