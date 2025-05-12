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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostJpaRepository extends JpaRepository<PostEntity, Long> {

    List<PostEntity> findAllByUserEntityId(Long userId);

    @EntityGraph(attributePaths = {"userEntity", "postImages"})
    Optional<PostEntity> findById(Long id);

    // 전체 조회
    @Query("SELECT p FROM PostEntity p ORDER BY p.createdAt DESC")
    Page<PostEntity> findAllPosts(Pageable pageable);
    @Query("SELECT p FROM PostEntity p ORDER BY p.likeCount DESC, p.createdAt DESC")
    Page<PostEntity> findAllPostsByLike(Pageable pageable);
    @Query("SELECT p FROM PostEntity p ORDER BY p.viewCount DESC, p.createdAt DESC")
    Page<PostEntity> findAllPostsByView(Pageable pageable);

    // 카테고리별 조회
    @Query("SELECT p FROM PostEntity p WHERE p.type = :category ORDER BY p.createdAt DESC")
    Page<PostEntity> findByCategory(@Param("category") PostType category, Pageable pageable);
    @Query("SELECT p FROM PostEntity p WHERE p.type = :category ORDER BY p.likeCount DESC, p.createdAt DESC")
    Page<PostEntity> findByCategoryByLike(@Param("category") PostType category, Pageable pageable);
    @Query("SELECT p FROM PostEntity p WHERE p.type = :category ORDER BY p.viewCount DESC, p.createdAt DESC")
    Page<PostEntity> findByCategoryByView(@Param("category") PostType category, Pageable pageable);

    // 커서 기반 조회
    @Query("SELECT p FROM PostEntity p WHERE p.type = :category AND p.id < :lastPostId ORDER BY p.id DESC")
    Page<PostEntity> findByCategoryAndCursor(
            @Param("category") PostType category,
            @Param("lastPostId") Long lastPostId,
            Pageable pageable
    );


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

}
