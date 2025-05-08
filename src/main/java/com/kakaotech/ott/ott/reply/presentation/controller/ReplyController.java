package com.kakaotech.ott.ott.reply.presentation.controller;

import com.kakaotech.ott.ott.global.response.ApiResponse;
import com.kakaotech.ott.ott.reply.presentation.dto.request.ReplyCreateRequestDto;
import com.kakaotech.ott.ott.reply.presentation.dto.response.ReplyCreateResponseDto;
import com.kakaotech.ott.ott.user.domain.model.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReplyController {

    @PostMapping("/comments/{commentsId}/replies")
    public ResponseEntity<ApiResponse<ReplyCreateResponseDto>> createReply(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody @Valid ReplyCreateRequestDto replyCreateRequestDto,
            @PathVariable Long commentsId) {

        Long userId = userPrincipal.getId();

        ReplyCreateResponseDto replyCreateResponseDto = new ReplyCreateResponseDto();

        return ResponseEntity.ok(ApiResponse.success("대댓글 작성 완료", replyCreateResponseDto));
    }
}
