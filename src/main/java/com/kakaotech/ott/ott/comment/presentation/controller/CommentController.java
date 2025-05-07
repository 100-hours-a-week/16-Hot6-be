package com.kakaotech.ott.ott.comment.presentation.controller;

import com.kakaotech.ott.ott.comment.application.service.CommentService;
import com.kakaotech.ott.ott.comment.presentation.dto.request.CommentRequestDto;
import com.kakaotech.ott.ott.comment.presentation.dto.response.CommentResponseDto;
import com.kakaotech.ott.ott.global.response.ApiResponse;
import com.kakaotech.ott.ott.user.domain.model.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentResponseDto>> CreateComment(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                         @RequestBody @Valid CommentRequestDto commentRequestDto,
                                                                         @PathVariable Long postId) {

        Long userId = userPrincipal.getId();

        CommentResponseDto commentResponseDto = commentService.createComment(commentRequestDto, userId, postId);

        return ResponseEntity.ok(ApiResponse.success("댓글 작성 완료", commentResponseDto));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse> deleteComment(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                     @PathVariable Long commentId) {

        Long userId = userPrincipal.getId();

        commentService.deleteComment(commentId, userId);

        return ResponseEntity.ok(ApiResponse.success("댓글 삭제 완료", null));
    }

    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse> updateComment(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                     @RequestBody @Valid CommentRequestDto commentRequestDto,
                                                     @PathVariable Long commentId) {

        Long userId = userPrincipal.getId();

        CommentResponseDto commentResponseDto = commentService.updateComment(commentRequestDto, commentId, userId);

        return ResponseEntity.ok(ApiResponse.success("댓글 수정 완료", commentResponseDto));
    }
}
