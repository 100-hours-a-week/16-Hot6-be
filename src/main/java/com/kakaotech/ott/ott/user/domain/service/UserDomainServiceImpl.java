package com.kakaotech.ott.ott.user.domain.service;

import com.kakaotech.ott.ott.user.domain.repository.UserRepository;
import com.kakaotech.ott.ott.user.application.service.UserDomainService;
import com.kakaotech.ott.ott.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDomainServiceImpl implements UserDomainService {

    private final UserRepository userRepository;

    /**
     * OAuth로 전달된 사용자 정보를 기반으로 사용자 저장 또는 조회
     */
    @Override
    public User saveOrGetKakaoUser(Map<String, Object> attributes) {
        String email = extractEmail(attributes);
        String nickname = extractNickname(attributes);
        String imagePath = "/default/profile.jpg"; // 기본 이미지

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }

        // User 도메인 객체 생성
        User user = User.createUser(email, nickname, imagePath);

        // 저장 후 반환
        return userRepository.save(user);
    }

    private String extractEmail(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        return (String) kakaoAccount.get("email");
    }

    private String extractNickname(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        return (String) profile.get("nickname");
    }
}
