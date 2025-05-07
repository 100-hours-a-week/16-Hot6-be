package com.kakaotech.ott.ott.comment.presentation.controller;

import com.kakaotech.ott.ott.comment.application.service.CommentService;
import com.kakaotech.ott.ott.comment.presentation.dto.request.CommentCreateRequestDto;
import com.kakaotech.ott.ott.comment.presentation.dto.response.CommentCreateResponseDto;
import com.kakaotech.ott.ott.comment.presentation.dto.response.CommentListResponseDto;
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

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentListResponseDto>> getComments(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long postId,
            @RequestParam(required = false) Long lastCommentId,
            @RequestParam(defaultValue = "10") int size) {

        Long userId = userPrincipal.getId();

        CommentListResponseDto commentListResponseDto = commentService.findByPostIdCursor(userId, postId, lastCommentId, size);
        return ResponseEntity.ok(ApiResponse.success("댓글 조회 완료", commentListResponseDto));
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentCreateResponseDto>> CreateComment(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                         @RequestBody @Valid CommentCreateRequestDto commentCreateRequestDto,
                                                                         @PathVariable Long postId) {

        Long userId = userPrincipal.getId();

        CommentCreateResponseDto commentCreateResponseDto = commentService.createComment(commentCreateRequestDto, userId, postId);

        return ResponseEntity.ok(ApiResponse.success("댓글 작성 완료", commentCreateResponseDto));
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
                                                     @RequestBody @Valid CommentCreateRequestDto commentCreateRequestDto,
                                                     @PathVariable Long commentId) {

        Long userId = userPrincipal.getId();

        CommentCreateResponseDto commentCreateResponseDto = commentService.updateComment(commentCreateRequestDto, commentId, userId);

        return ResponseEntity.ok(ApiResponse.success("댓글 수정 완료", commentCreateResponseDto));
    }
}
