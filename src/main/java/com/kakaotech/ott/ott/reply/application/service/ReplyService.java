package com.kakaotech.ott.ott.reply.application.service;

import com.kakaotech.ott.ott.reply.presentation.dto.request.ReplyCreateRequestDto;
import com.kakaotech.ott.ott.reply.presentation.dto.response.ReplyCreateResponseDto;
import com.kakaotech.ott.ott.reply.presentation.dto.response.ReplyListResponseDto;

public interface ReplyService {

    ReplyListResponseDto getAllReply(Long userId, Long commentId, Long lastReplyId, int size);

    ReplyCreateResponseDto createReply(ReplyCreateRequestDto replyCreateRequestDto, Long userId, Long commentId);

    ReplyCreateResponseDto updateReply(ReplyCreateRequestDto replyCreateRequestDto, Long replyId, Long userId);

    void deleteReply(Long replyId, Long userId);
}
