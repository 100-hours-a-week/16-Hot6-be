package com.kakaotech.ott.ott.comment.domain.repository;

import com.kakaotech.ott.ott.comment.infrastructure.entity.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentJpaRepository extends JpaRepository<CommentEntity, Long> {

    Page<CommentEntity> findAllByPostEntityIdOrderByIdDesc(Long postId, Pageable pageable);

    Page<CommentEntity> findAllByPostEntityIdAndIdLessThanOrderByIdDesc(Long postId, Long lastCommentId, Pageable pageable);

    @Query("SELECT COUNT(c) FROM CommentEntity c WHERE c.post.id = :postId")
    int countByPostId(@Param("postId") Long postId);

}
