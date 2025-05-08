package com.kakaotech.ott.ott.reply.application.service;

import com.kakaotech.ott.ott.reply.presentation.dto.request.ReplyCreateRequestDto;
import com.kakaotech.ott.ott.reply.presentation.dto.response.ReplyCreateResponseDto;

public interface ReplyService {

    ReplyCreateResponseDto createReply(ReplyCreateRequestDto replyCreateRequestDto, Long userId, Long commentId);
}
