package com.kakaotech.ott.ott.user.domain.service;

import com.kakaotech.ott.ott.pointHistory.domain.model.PointActionReason;
import com.kakaotech.ott.ott.pointHistory.domain.model.PointActionType;
import com.kakaotech.ott.ott.pointHistory.domain.model.PointHistory;
import com.kakaotech.ott.ott.pointHistory.domain.repository.PointHistoryRepository;
import com.kakaotech.ott.ott.user.domain.repository.UserAuthRepository;
import com.kakaotech.ott.ott.user.application.service.UserDomainService;
import com.kakaotech.ott.ott.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDomainServiceImpl implements UserDomainService {

    private final UserAuthRepository userAuthRepository;
    private final PointHistoryRepository pointHistoryRepository;

    /**
     * OAuth로 전달된 사용자 정보를 기반으로 사용자 저장 또는 조회
     */
    @Override
    @Transactional
    public User saveOrGetKakaoUser(Map<String, Object> attributes) {
        String email = extractEmail(attributes);
        String nickname = extractNickname(attributes);
        String imagePath = extractProfileImage(attributes); // ✅ 프로필 이미지 추가

        Optional<User> optionalUser = userAuthRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }

        // User 도메인 객체 생성
        User user = User.createUser(email, nickname, imagePath);

        PointHistory pointHistory = PointHistory.createPointHistory(user.getId(), 500, 500, PointActionType.EARN, PointActionReason.SIGNUP);
        pointHistoryRepository.save(pointHistory, user);

        // 저장 후 반환
        return userAuthRepository.save(user);
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

    private String extractProfileImage(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        return (String) profile.get("profile_image_url");
    }
}
