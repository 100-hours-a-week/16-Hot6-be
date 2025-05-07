package com.kakaotech.ott.ott.comment.application.service;

import com.kakaotech.ott.ott.comment.presentation.dto.request.CommentRequestDto;
import com.kakaotech.ott.ott.comment.presentation.dto.response.CommentResponseDto;

public interface CommentService {

    CommentResponseDto createComment(CommentRequestDto commentRequestDto, Long userId, Long postId);

    void deleteComment(Long userId, Long commentId);

    CommentResponseDto updateComment(CommentRequestDto commentRequestDto, Long commendId, Long userId);
}
