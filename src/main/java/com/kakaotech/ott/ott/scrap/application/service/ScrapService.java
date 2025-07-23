package com.kakaotech.ott.ott.scrap.application.service;

import com.kakaotech.ott.ott.scrap.presentation.dto.request.ScrapRequestDto;

public interface ScrapService {

    void toggleScrap(Long userId, ScrapRequestDto scrapRequestDto);

}
