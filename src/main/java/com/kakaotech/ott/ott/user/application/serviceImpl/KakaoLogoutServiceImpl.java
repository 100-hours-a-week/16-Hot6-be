package com.kakaotech.ott.ott.user.application.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KakaoLogoutServiceImpl {

    private final RestTemplate restTemplate;

    public void logoutFromKakao(String kakaoAccessToken) {
        String logoutUrl = "https://kapi.kakao.com/v1/user/logout";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(kakaoAccessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.postForEntity(logoutUrl, request, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("카카오 로그아웃 성공");
        } else {
            System.out.println("카카오 로그아웃 실패: " + response.getBody());
        }
    }
}
