package com.kakaotech.ott.ott.user.application.serviceImpl;

import com.kakaotech.ott.ott.aiImage.domain.model.AiImage;
import com.kakaotech.ott.ott.aiImage.domain.repository.AiImageRepository;
import com.kakaotech.ott.ott.user.application.service.UserService;
import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.domain.repository.UserRepository;
import com.kakaotech.ott.ott.user.presentation.dto.request.UserInfoUpdateRequestDto;
import com.kakaotech.ott.ott.user.presentation.dto.response.MyDeskImageResponseDto;
import com.kakaotech.ott.ott.user.presentation.dto.response.MyInfoResponseDto;
import com.kakaotech.ott.ott.user.presentation.dto.response.UserInfoUpdateResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AiImageRepository aiImageRepository;

    @Override
    public MyInfoResponseDto getMyInfo(Long userId) {

        User user = userRepository.findById(userId);

        return new MyInfoResponseDto(user.getNicknameCommunity(), user.getNicknameKakao(), user.getImagePath(), user.getPoint(), user.isVerified());
    }

    @Override
    public MyDeskImageResponseDto getMyDeskWithCursor(Long userId, LocalDateTime createdAtCursor, Long lastId, int size) {
        Slice<AiImage> aiImages = aiImageRepository.findUserDeskImages(
                userId,
                createdAtCursor,
                lastId,
                size
        );

        List<MyDeskImageResponseDto.ImageDto> imageDtos = aiImages.stream()
                .map(image -> new MyDeskImageResponseDto.ImageDto(
                        image.getId(),
                        image.getBeforeImagePath(),
                        image.getAfterImagePath(),
                        image.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return new MyDeskImageResponseDto(
                imageDtos,
                size,
                aiImages.hasNext() ? aiImages.getContent().get(aiImages.getNumberOfElements() - 1).getId() : null,
                aiImages.hasNext()
        );
    }

    @Override
    public UserInfoUpdateResponseDto updateUserInfo(Long userId, UserInfoUpdateRequestDto userInfoUpdateRequestDto) {

        User user = userRepository.findById(userId);

        if(userInfoUpdateRequestDto.getProfileImage() != null) user.updateProfileImagePath(userInfoUpdateRequestDto.getProfileImage());
        if(userInfoUpdateRequestDto.getNicknameCommunity() != null) user.updateNicknameCommunity(userInfoUpdateRequestDto.getNicknameCommunity());
        if(userInfoUpdateRequestDto.getNicknameKakao() != null) user.updateNicknameKakao(userInfoUpdateRequestDto.getNicknameKakao());

        User savedUser = userRepository.save(user);

        return new UserInfoUpdateResponseDto(savedUser.getImagePath(), savedUser.getNicknameCommunity(), savedUser.getNicknameKakao());
    }
}
