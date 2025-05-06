package com.kakaotech.ott.ott.post.domain.repository;

import com.kakaotech.ott.ott.post.infrastructure.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostJpaRepository extends JpaRepository<PostEntity, Long> {

    List<PostEntity> findAllByUserEntityId(Long userId);

    @EntityGraph(attributePaths = {"userEntity", "postImages"})
    Optional<PostEntity> findById(Long id);

    // 첫 조회(마지막 커서가 없을 때) – 가장 최근 게시글 size개
    Page<PostEntity> findAllByOrderByIdDesc(Pageable pageable);

    // 이후 조회(커서가 있을 때) – id < lastPostId 인 게시글 size개
    Page<PostEntity> findAllByIdLessThanOrderByIdDesc(Long lastPostId, Pageable pageable);

    @Modifying
    @Query("UPDATE PostEntity p SET p.viewCount = p.viewCount + :delta WHERE p.id = :postId")
    void incrementViewCount(@Param("postId") Long postId, @Param("delta") Long delta);

}
