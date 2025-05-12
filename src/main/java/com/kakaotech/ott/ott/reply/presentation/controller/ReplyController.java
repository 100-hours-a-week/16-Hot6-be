package com.kakaotech.ott.ott.reply.presentation.controller;

import com.kakaotech.ott.ott.global.response.ApiResponse;
import com.kakaotech.ott.ott.reply.application.service.ReplyService;
import com.kakaotech.ott.ott.reply.presentation.dto.request.ReplyCreateRequestDto;
import com.kakaotech.ott.ott.reply.presentation.dto.response.ReplyCreateResponseDto;
import com.kakaotech.ott.ott.reply.presentation.dto.response.ReplyListResponseDto;
import com.kakaotech.ott.ott.user.domain.model.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    @GetMapping("/comments/{commentId}/replies")
    public ResponseEntity<ApiResponse<ReplyListResponseDto>> getAllReply(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long commentId,
            @RequestParam(required = false) Long lastReplyId,
            @RequestParam(defaultValue = "5") int size) {

        Long userId = userPrincipal.getId();

        ReplyListResponseDto replyListResponseDto = replyService.getAllReply(userId, commentId, lastReplyId, size);
        return ResponseEntity.ok(ApiResponse.success("답글 목록 조회 성공", replyListResponseDto));
    }

    @PostMapping("/comments/{commentId}/replies")
    public ResponseEntity<ApiResponse<ReplyCreateResponseDto>> createReply(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody @Valid ReplyCreateRequestDto replyCreateRequestDto,
            @PathVariable Long commentId) {

        Long userId = userPrincipal.getId();

        ReplyCreateResponseDto replyCreateResponseDto = replyService.createReply(replyCreateRequestDto, userId, commentId);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("답글 작성 완료", replyCreateResponseDto));
    }

    @PatchMapping("/replies/{repliesId}")
    public ResponseEntity<ApiResponse<ReplyCreateResponseDto>> updateReply(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody @Valid ReplyCreateRequestDto replyCreateRequestDto,
            @PathVariable Long repliesId) {

        Long userId = userPrincipal.getId();

        ReplyCreateResponseDto replyCreateResponseDto = replyService.updateReply(replyCreateRequestDto, repliesId, userId);

        return ResponseEntity.ok(ApiResponse.success("대댓글 수정 완료", replyCreateResponseDto));
    }

    @DeleteMapping("/replies/{repliesId}")
    public ResponseEntity<ApiResponse> deleteReply(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long repliesId) {

        Long userId = userPrincipal.getId();

        replyService.deleteReply(repliesId, userId);

        return ResponseEntity.ok(ApiResponse.success("대댓글 삭제 완료", null));
    }
}
