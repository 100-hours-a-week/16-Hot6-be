package com.kakaotech.ott.ott.user.application.serviceImpl;

import com.kakaotech.ott.ott.user.domain.repository.UserRepository;
import com.kakaotech.ott.ott.user.infrastructure.entity.UserEntity;
import com.kakaotech.ott.ott.user.application.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public boolean checkQuota(Long userId) {

        LocalDate today = LocalDate.now();

        // 1. 사용자가 존재하지 않으면 예외 발생 (404)
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자를 찾을 수 없습니다."));

        // 2. quota 사용일이 null이면 → 아직 사용 안 함 → 사용 가능
        LocalDate lastGenerated = userEntity.getAiImageGeneratedDate();

        return lastGenerated == null || !today.equals(lastGenerated);

    }
}
