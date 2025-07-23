package com.kakaotech.ott.ott.like.application.service;

import com.kakaotech.ott.ott.like.presentation.dto.request.LikeRequestDto;

public interface LikeService {

    void toggleLike(Long userId, LikeRequestDto likeRequestDto);
}
