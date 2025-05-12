package com.kakaotech.ott.ott.reply.domain.repository;

import com.kakaotech.ott.ott.reply.infrastructure.entity.ReplyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyJpaRepository extends JpaRepository<ReplyEntity, Long> {

    Page<ReplyEntity> findAllByCommentEntityIdOrderByIdDesc(Long commentId, Pageable pageable);

    Page<ReplyEntity> findAllByCommentEntityIdAndIdLessThanOrderByIdDesc(Long commentId, Long lastReplyId, Pageable pageable);

}
