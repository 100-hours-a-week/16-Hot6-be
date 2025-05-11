package com.kakaotech.ott.ott.reply.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.comment.domain.repository.CommentJpaRepository;
import com.kakaotech.ott.ott.comment.infrastructure.entity.CommentEntity;
import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.reply.domain.model.Reply;
import com.kakaotech.ott.ott.reply.domain.repository.ReplyJpaRepository;
import com.kakaotech.ott.ott.reply.domain.repository.ReplyRepository;
import com.kakaotech.ott.ott.reply.infrastructure.entity.ReplyEntity;
import com.kakaotech.ott.ott.user.domain.repository.UserJpaRepository;
import com.kakaotech.ott.ott.user.infrastructure.entity.UserEntity;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReplyRepositoryImpl implements ReplyRepository {

    private final ReplyJpaRepository replyJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final CommentJpaRepository commentJpaRepository;

    @Override
    public List<Reply> findByCommentIdCursor(Long commentId, Long lastReplyId, int size) {
        Pageable pg = PageRequest.of(0, size, Sort.by("id").descending());
        Page<ReplyEntity> page;
        if (lastReplyId == null) {
            page = replyJpaRepository.findAllByCommentEntityIdOrderByIdDesc(commentId, pg);
        } else {
            page = replyJpaRepository.findAllByCommentEntityIdAndIdLessThanOrderByIdDesc(commentId, lastReplyId, pg);
        }

        return page.stream()
                .map(ReplyEntity::toDomain)
                .toList();
    }

    @Override
    public Reply save(Reply reply) {

        UserEntity userEntity = userJpaRepository.findById(reply.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        CommentEntity commentEntity = commentJpaRepository.findById(reply.getCommentId())
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        ReplyEntity replyEntity = ReplyEntity.from(reply, userEntity, commentEntity);

        return replyJpaRepository.save(replyEntity).toDomain();
    }

    @Override
    public void delete(Long replyId) {

        replyJpaRepository.deleteById(replyId);
    }

    @Override
    public Reply findById(Long replyId) {

        return replyJpaRepository.findById(replyId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPLY_NOT_FOUND))
                .toDomain();
    }
}
