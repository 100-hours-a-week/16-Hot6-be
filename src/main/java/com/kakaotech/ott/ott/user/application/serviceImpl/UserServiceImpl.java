package com.kakaotech.ott.ott.user.application.serviceImpl;

import com.kakaotech.ott.ott.aiImage.application.service.ImageUploader;
import com.kakaotech.ott.ott.aiImage.domain.model.AiImage;
import com.kakaotech.ott.ott.aiImage.domain.repository.AiImageRepository;
import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.user.application.service.UserService;
import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.domain.repository.UserRepository;
import com.kakaotech.ott.ott.user.presentation.dto.request.UserInfoUpdateRequestDto;
import com.kakaotech.ott.ott.user.presentation.dto.request.UserVerifiedRequestDto;
import com.kakaotech.ott.ott.user.presentation.dto.response.MyDeskImageResponseDto;
import com.kakaotech.ott.ott.user.presentation.dto.response.MyInfoResponseDto;
import com.kakaotech.ott.ott.user.presentation.dto.response.UserInfoUpdateResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AiImageRepository aiImageRepository;
    private final ImageUploader imageUploader;

    @Value("${verified.code}")
    private String verifiedCode;

    @Value("${cloud.aws.s3.base-url}")
    private String baseUrl;

    @Override
    public MyInfoResponseDto getMyInfo(Long userId) {

        User user = userRepository.findById(userId);

        if (!user.isActive())
            throw new CustomException(ErrorCode.USER_DELETED);

        return new MyInfoResponseDto(user.getNicknameCommunity(), user.getNicknameKakao(), user.getImagePath(), user.getPoint(), user.isVerified());
    }

    @Override
    public MyDeskImageResponseDto getMyDeskWithCursor(Long userId, Long lastId, int size, String type) {

        User user = userRepository.findById(userId);

        if (!user.isActive())
            throw new CustomException(ErrorCode.USER_DELETED);

        Slice<AiImage> aiImages = aiImageRepository.findUserDeskImages(
                userId,
                lastId,
                size,
                type
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
    public UserInfoUpdateResponseDto updateUserInfo(Long userId, UserInfoUpdateRequestDto userInfoUpdateRequestDto) throws IOException {

        User user = userRepository.findById(userId);

        if (!user.isActive())
            throw new CustomException(ErrorCode.USER_DELETED);

//        if (userRepository.existsByNicknameCommunity(userInfoUpdateRequestDto.getNicknameCommunity()))
//            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME_COMMUNITY);

        if(userInfoUpdateRequestDto.getProfileImagePath() != null) {

            MultipartFile afterProfileImage = userInfoUpdateRequestDto.getProfileImagePath();

            String beforeProfileImage = user.getImagePath();
            imageUploader.delete(beforeProfileImage);

            String afterProfileImageUrl = baseUrl + imageUploader.upload(afterProfileImage);

            user.updateProfileImagePath(afterProfileImageUrl);
        }
        if(userInfoUpdateRequestDto.getNicknameCommunity() != null) {
            user.updateNicknameCommunity(userInfoUpdateRequestDto.getNicknameCommunity());
        }
        // 카카오 닉네임 변경 (인증된 사용자만)
        if (userInfoUpdateRequestDto.getNicknameKakao() != null) {
            if (user.isVerified()) {
                user.updateNicknameKakao(userInfoUpdateRequestDto.getNicknameKakao());
            } else {
                throw new CustomException(ErrorCode.USER_NOT_VERIFIED);
            }
        }

        User savedUser = userRepository.update(user);

        return new UserInfoUpdateResponseDto(savedUser.getImagePath(), savedUser.getNicknameCommunity(), savedUser.getNicknameKakao());
    }

    @Override
    public void deleteUser(Long userId) {

        User user = userRepository.findById(userId);

        if(!user.isActive())
            throw new CustomException(ErrorCode.USER_DELETED);

        user.updateActive(false);
        user.updateDeletedAt(LocalDateTime.now());

        userRepository.delete(user);
    }

    @Override
    public void verifiedCode(Long userId, UserVerifiedRequestDto userVerifiedRequestDto) {

        User user = userRepository.findById(userId);

        if(!user.isActive())
            throw new CustomException(ErrorCode.USER_DELETED);

        if(user.isVerified())
            throw new CustomException(ErrorCode.USER_ALREADY_AUTHENTICATED);

        if(!userVerifiedRequestDto.getCode().equals(verifiedCode))
            throw new CustomException(ErrorCode.INVALID_INPUT_CODE);

        user.updateVerified();
        userRepository.certify(user);
    }

    @Override
    public void recoverUser(Long userId) {

        User user = userRepository.findById(userId);

        if(user.isActive())
            throw new CustomException(ErrorCode.USER_FORBIDDEN);

        user.updateActive(true);
        user.updateDeletedAt(null);

        userRepository.recovery(user);
    }


}
