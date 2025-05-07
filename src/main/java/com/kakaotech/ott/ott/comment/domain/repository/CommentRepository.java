package com.kakaotech.ott.ott.comment.domain.repository;

import com.kakaotech.ott.ott.comment.domain.model.Comment;

import java.util.List;

public interface CommentRepository {

    Comment save(Comment comment);

    void deleteComment(Long commentId);

    Comment findById(Long commentId);

    List<Comment> findByPostIdCursor(Long postId, Long lastCommentId, int size);
}
