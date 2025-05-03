package com.kakaotech.ott.ott.aiImage.application.serviceImpl;

import com.kakaotech.ott.ott.aiImage.application.service.FastApiClient;
import com.kakaotech.ott.ott.aiImage.presentation.dto.request.FastApiRequestDto;
import com.kakaotech.ott.ott.aiImage.presentation.dto.response.FastApiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

@Service
@RequiredArgsConstructor
public class FastApiClientImpl implements FastApiClient {

    private final RestTemplate restTemplate;

    @Override
    public FastApiResponseDto sendBeforeImageToFastApi(FastApiRequestDto fastApiRequestDto) {
        String url = "https://dev-ai.onthe-top.com/classify";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<FastApiRequestDto> request = new HttpEntity<>(fastApiRequestDto, headers);

        return restTemplate.postForObject(url, request, FastApiResponseDto.class);
    }
}
