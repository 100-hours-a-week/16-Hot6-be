package com.kakaotech.ott.ott.comment.domain.repository;

import com.kakaotech.ott.ott.comment.infrastructure.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentJpaRepository extends JpaRepository<CommentEntity, Long> {
}
