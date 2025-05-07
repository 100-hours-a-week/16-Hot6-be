package com.kakaotech.ott.ott.scrap.application.service;

import com.kakaotech.ott.ott.like.presentation.dto.request.LikeRequestDto;
import com.kakaotech.ott.ott.scrap.presentation.dto.request.ScrapRequestDto;

public interface ScrapService {

    void likeScrap(Long userId, ScrapRequestDto scrapRequestDto);

    void unlikeScrap(Long userId, ScrapRequestDto scrapRequestDto);
}
