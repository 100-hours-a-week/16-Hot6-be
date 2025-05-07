package com.kakaotech.ott.ott.comment.domain.repository;

import com.kakaotech.ott.ott.comment.infrastructure.entity.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentJpaRepository extends JpaRepository<CommentEntity, Long> {

    Page<CommentEntity> findAllByPostEntityIdOrderByIdDesc(Long postId, Pageable pageable);

    Page<CommentEntity> findAllByPostEntityIdAndIdLessThanOrderByIdDesc(Long postId, Long lastCommentId, Pageable pageable);

}
