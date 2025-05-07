package com.kakaotech.ott.ott.comment.domain.repository;

import com.kakaotech.ott.ott.comment.domain.model.Comment;

public interface CommentRepository {

    Comment save(Comment comment);

    void deleteComment(Long commentId);

    Comment findById(Long commentId);
}
