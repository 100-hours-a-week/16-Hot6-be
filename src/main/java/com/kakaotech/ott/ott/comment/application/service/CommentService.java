package com.kakaotech.ott.ott.comment.application.service;

import com.kakaotech.ott.ott.comment.presentation.dto.request.CommentCreateRequestDto;
import com.kakaotech.ott.ott.comment.presentation.dto.response.CommentCreateResponseDto;
import com.kakaotech.ott.ott.comment.presentation.dto.response.CommentListResponseDto;

public interface CommentService {

    CommentCreateResponseDto createComment(CommentCreateRequestDto commentCreateRequestDto, Long userId, Long postId);

    void deleteComment(Long userId, Long commentId);

    CommentCreateResponseDto updateComment(CommentCreateRequestDto commentCreateRequestDto, Long commendId, Long userId);

    CommentListResponseDto findByPostIdCursor(Long userId, Long postId, Long lastCommentId, int size);
}
