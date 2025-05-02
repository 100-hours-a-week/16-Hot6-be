package com.kakaotech.ott.ott.aiImage.application.serviceImpl;

import com.kakaotech.ott.ott.aiImage.application.service.FastApiClient;
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
    public FastApiResponseDto sendBeforeImageToFastApi(String imageName) {
        String url = "http://localhost:8080/classify";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);

        HttpEntity<String> request = new HttpEntity<>(imageName, headers);

        return restTemplate.postForObject(url, request, FastApiResponseDto.class);
    }
}
