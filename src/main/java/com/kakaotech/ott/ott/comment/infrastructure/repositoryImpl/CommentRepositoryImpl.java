package com.kakaotech.ott.ott.comment.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.comment.domain.model.Comment;
import com.kakaotech.ott.ott.comment.domain.repository.CommentJpaRepository;
import com.kakaotech.ott.ott.comment.domain.repository.CommentRepository;
import com.kakaotech.ott.ott.comment.infrastructure.entity.CommentEntity;
import com.kakaotech.ott.ott.post.domain.repository.PostJpaRepository;
import com.kakaotech.ott.ott.post.infrastructure.entity.PostEntity;
import com.kakaotech.ott.ott.user.domain.repository.UserJpaRepository;
import com.kakaotech.ott.ott.user.infrastructure.entity.UserEntity;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepository {

    private final CommentJpaRepository commentJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final PostJpaRepository postJpaRepository;

    @Override
    @Transactional
    public Comment save(Comment comment) {

        UserEntity userEntity = userJpaRepository.findById(comment.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자가 존재하지 않습니다."));

        PostEntity postEntity = postJpaRepository.findById(comment.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("해당 게시글이 존재하지 않습니다."));

        CommentEntity commentEntity = CommentEntity.from(comment, userEntity, postEntity);

        return commentJpaRepository.save(commentEntity).toDomain();
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {

        commentJpaRepository.deleteById(commentId);
    }

    @Override
    public Comment findById(Long commentId) {

        return commentJpaRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글이 존재하지 않습니다."))
                .toDomain();
    }
}
