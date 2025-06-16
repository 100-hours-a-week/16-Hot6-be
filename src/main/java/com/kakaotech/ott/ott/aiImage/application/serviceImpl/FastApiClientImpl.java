package com.kakaotech.ott.ott.aiImage.application.serviceImpl;

import com.kakaotech.ott.ott.aiImage.application.service.FastApiClient;
import com.kakaotech.ott.ott.aiImage.presentation.dto.request.FastApiRequestDto;
import com.kakaotech.ott.ott.aiImage.presentation.dto.response.FastApiResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

@Service
@Slf4j
@RequiredArgsConstructor
public class FastApiClientImpl implements FastApiClient {

    private final RestTemplate restTemplate;

    @Value("${cloud.aws.s3.base-url}")
    private String baseUrl;

    @Value("${fastapi.URL}")
    private String aiBaseUrl;

    @Override
    public FastApiResponseDto sendBeforeImageToFastApi(FastApiRequestDto fastApiRequestDto) {
        String url = aiBaseUrl + "classify";

        fastApiRequestDto.fastApiUrl(baseUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<FastApiRequestDto> request = new HttpEntity<>(fastApiRequestDto, headers);

        try {
            return restTemplate.postForObject(url, request, FastApiResponseDto.class);
        } catch (HttpServerErrorException e) {
            log.error("FastAPI 응답 오류 (500): {}", e.getResponseBodyAsString());
            throw new RuntimeException("FastAPI 호출 중 오류 발생: " + e.getMessage());
        }

    }
}
