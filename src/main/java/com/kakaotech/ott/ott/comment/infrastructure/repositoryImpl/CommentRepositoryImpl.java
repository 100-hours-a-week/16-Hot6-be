package com.kakaotech.ott.ott.comment.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.comment.domain.model.Comment;
import com.kakaotech.ott.ott.comment.domain.repository.CommentJpaRepository;
import com.kakaotech.ott.ott.comment.domain.repository.CommentRepository;
import com.kakaotech.ott.ott.comment.infrastructure.entity.CommentEntity;
import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.post.domain.repository.PostJpaRepository;
import com.kakaotech.ott.ott.post.infrastructure.entity.PostEntity;
import com.kakaotech.ott.ott.user.domain.repository.UserJpaRepository;
import com.kakaotech.ott.ott.user.infrastructure.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        PostEntity postEntity = postJpaRepository.findById(comment.getPostId())
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        CommentEntity commentEntity = CommentEntity.from(comment, userEntity, postEntity);

        return commentJpaRepository.save(commentEntity).toDomain();
    }

    @Override
    public void deleteComment(Long commentId) {

        commentJpaRepository.deleteById(commentId);
        commentJpaRepository.flush();
    }

    @Override
    public Comment findById(Long commentId) {

        return commentJpaRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND))
                .toDomain();
    }

    @Override
    public List<Comment> findByPostIdCursor(Long postId, Long lastCommentId, int size) {
        Pageable pg = PageRequest.of(0, size, Sort.by("id").descending());
        Page<CommentEntity> page;
        if (lastCommentId == null) {
            page = commentJpaRepository.findAllByPostEntityIdOrderByIdDesc(postId, pg);
        } else {
            page = commentJpaRepository.findAllByPostEntityIdAndIdLessThanOrderByIdDesc(postId, lastCommentId, pg);
        }

        return page.stream()
                .map(CommentEntity::toDomain)
                .toList();
    }
}
