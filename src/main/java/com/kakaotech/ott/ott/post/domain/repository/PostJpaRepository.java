package com.kakaotech.ott.ott.post.domain.repository;

import com.kakaotech.ott.ott.post.domain.model.PostType;
import com.kakaotech.ott.ott.post.infrastructure.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PostJpaRepository extends JpaRepository<PostEntity, Long> {

    @EntityGraph(attributePaths = {"userEntity", "postImages"})
    Optional<PostEntity> findById(Long id);

    // 최신순 조회 (커서 기반)
    @Query("SELECT DISTINCT p FROM PostEntity p WHERE (:lastPostId IS NULL OR p.id < :lastPostId) ORDER BY p.id DESC")
    Page<PostEntity> findAllPosts(@Param("lastPostId") Long lastPostId, Pageable pageable);

    // 좋아요 순 조회 (커서 기반)
    @Query("SELECT DISTINCT p FROM PostEntity p WHERE (:lastPostId IS NULL OR p.id < :lastPostId) ORDER BY p.likeCount DESC, p.id DESC")
    Page<PostEntity> findAllPostsByLike(@Param("lastPostId") Long lastPostId, Pageable pageable);

    // 조회수 순 조회 (커서 기반)
    @Query("SELECT DISTINCT p FROM PostEntity p WHERE (:lastPostId IS NULL OR p.id < :lastPostId) ORDER BY p.viewCount DESC, p.id DESC")
    Page<PostEntity> findAllPostsByView(@Param("lastPostId") Long lastPostId, Pageable pageable);

    // 카테고리별 최신순 조회 (커서 기반)
    @Query("SELECT DISTINCT p FROM PostEntity p WHERE p.type = :category AND (:lastPostId IS NULL OR p.id < :lastPostId) ORDER BY p.id DESC")
    Page<PostEntity> findByCategory(@Param("category") PostType category, @Param("lastPostId") Long lastPostId, Pageable pageable);

    // 카테고리별 좋아요 순 조회 (커서 기반)
    @Query("SELECT DISTINCT p FROM PostEntity p WHERE p.type = :category AND (:lastPostId IS NULL OR p.id < :lastPostId) ORDER BY p.likeCount DESC, p.id DESC")
    Page<PostEntity> findByCategoryByLike(@Param("category") PostType category, @Param("lastPostId") Long lastPostId, Pageable pageable);

    // 카테고리별 조회수 순 조회 (커서 기반)
    @Query("SELECT DISTINCT p FROM PostEntity p WHERE p.type = :category AND (:lastPostId IS NULL OR p.id < :lastPostId) ORDER BY p.viewCount DESC, p.id DESC")
    Page<PostEntity> findByCategoryByView(@Param("category") PostType category, @Param("lastPostId") Long lastPostId, Pageable pageable);

    @Modifying
    @Query("UPDATE PostEntity p SET p.viewCount = p.viewCount + :delta WHERE p.id = :postId")
    void incrementViewCount(@Param("postId") Long postId, @Param("delta") Long delta);

    // 좋아요 카운트 증가·감소용
    @Modifying(clearAutomatically = true)
    @Query("UPDATE PostEntity p SET p.likeCount = p.likeCount + :delta WHERE p.id = :postId")
    void incrementLikeCount(@Param("postId") Long postId,
                            @Param("delta") Long delta);

    // 스크랩 카운트 증가·감소용
    @Modifying(clearAutomatically = true)
    @Query("UPDATE PostEntity p SET p.scrapCount = p.scrapCount + :delta WHERE p.id = :postId")
    void incrementScrapCount(@Param("postId") Long postId,
                            @Param("delta") Long delta);

    // 댓글 카운트 증가·감소용
    @Modifying(clearAutomatically = true)
    @Query("UPDATE PostEntity p SET p.commentCount = p.commentCount + :delta WHERE p.id = :postId")
    void incrementCommentCount(@Param("postId") Long postId,
                             @Param("delta") Long delta);

    // weight 기준 상위 7개 조회 (JPQL 사용, AI 이미지 LEFT JOIN)
    @Query("""
      SELECT DISTINCT p 
      FROM PostEntity p 
      LEFT JOIN FETCH p.postImages 
      LEFT JOIN AiImageEntity ai ON ai.postId = p.id 
      WHERE p.type = 'AI' 
      ORDER BY p.weight DESC
    """)
    List<PostEntity> findTop7ByTypeOrderByWeightDescWithAiImages(Pageable pageable);

    // ✅ Native Query를 통한 Batch Update (모든 게시글의 weight 계산)
    @Modifying
    @Transactional
    @Query(value = "UPDATE posts p " +
            "SET p.weight = (p.view_count * 0.8) + (p.scrap_count * 0.5) + (p.like_count * 0.3)",
            nativeQuery = true)
    void batchUpdateWeights();

}
