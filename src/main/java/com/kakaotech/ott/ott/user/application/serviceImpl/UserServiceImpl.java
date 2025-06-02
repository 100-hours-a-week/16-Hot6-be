package com.kakaotech.ott.ott.user.application.serviceImpl;

import com.kakaotech.ott.ott.aiImage.application.service.ImageUploader;
import com.kakaotech.ott.ott.aiImage.domain.model.AiImage;
import com.kakaotech.ott.ott.aiImage.domain.repository.AiImageRepository;
import com.kakaotech.ott.ott.comment.domain.repository.CommentRepository;
import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.like.domain.repository.LikeRepository;
import com.kakaotech.ott.ott.post.domain.model.MyDeskState;
import com.kakaotech.ott.ott.post.domain.model.Post;
import com.kakaotech.ott.ott.post.domain.repository.PostRepository;
import com.kakaotech.ott.ott.post.presentation.dto.response.PostAllResponseDto;
import com.kakaotech.ott.ott.post.presentation.dto.response.PostAuthorResponseDto;
import com.kakaotech.ott.ott.scrap.domain.model.ScrapType;
import com.kakaotech.ott.ott.scrap.domain.repository.ScrapRepository;
import com.kakaotech.ott.ott.user.application.service.UserService;
import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.domain.repository.UserRepository;
import com.kakaotech.ott.ott.user.presentation.dto.request.UserInfoUpdateRequestDto;
import com.kakaotech.ott.ott.user.presentation.dto.request.UserVerifiedRequestDto;
import com.kakaotech.ott.ott.user.presentation.dto.response.MyDeskImageResponseDto;
import com.kakaotech.ott.ott.user.presentation.dto.response.MyInfoResponseDto;
import com.kakaotech.ott.ott.user.presentation.dto.response.MyPostResponseDto;
import com.kakaotech.ott.ott.user.presentation.dto.response.UserInfoUpdateResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AiImageRepository aiImageRepository;
    private final ImageUploader imageUploader;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final ScrapRepository scrapRepository;

    @Value("${verified.code}")
    private String verifiedCode;

    @Value("${cloud.aws.s3.base-url}")
    private String baseUrl;

    @Override
    @Transactional(readOnly = true)
    public MyInfoResponseDto getMyInfo(Long userId) {

        User user = userRepository.findById(userId);

        if (!user.isActive())
            throw new CustomException(ErrorCode.USER_DELETED);

        return new MyInfoResponseDto(user.getNicknameCommunity(), user.getNicknameKakao(), user.getImagePath(), user.getPoint(), user.isVerified());
    }

    @Override
    @Transactional(readOnly = true)
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

        MyDeskState myDeskState;
        boolean hasAiImage = !aiImageRepository.findByUserId(userId).isEmpty(); // 이미지 존재 여부

        if (!hasAiImage)
            myDeskState = MyDeskState.NO_IMAGE_GENERATED;
        else if (hasAiImage && !aiImages.isEmpty())
            myDeskState = MyDeskState.IS_IMAGE_UNLINKED;
        else
            myDeskState = MyDeskState.ALL_POSTS_WRITTEN;

        return new MyDeskImageResponseDto(
                imageDtos,
                myDeskState,
                size,
                aiImages.hasNext() ? aiImages.getContent().get(aiImages.getNumberOfElements() - 1).getId() : null,
                aiImages.hasNext()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public MyPostResponseDto getMyPost(Long userId, Long lastId, int size) {
        // 게시글 페이징 조회 (커서 기반)
        Slice<Post> postSlice = postRepository.findUserPost(userId, lastId, size);

        // 게시글 ID 목록 수집
        List<Long> postIds = postSlice.getContent().stream()
                .map(Post::getId)
                .toList();

        // 좋아요/스크랩 정보를 한 번에 조회 (Batch)
        Set<Long> likedPostIds = likeRepository.findLikedPostIdsByUserId(userId, postIds);
        Set<Long> scrappedPostIds = scrapRepository.findScrappedPostIds(userId, postIds);
        User user = userRepository.findById(userId);

        // 게시글 DTO로 변환
        List<PostAllResponseDto.Posts> posts = postSlice.getContent().stream()
                .map(post -> {
                    String thumbnailImage = post.getImages().isEmpty()
                            ? null
                            : post.getImages().get(0).getImageUuid();
                    boolean liked = likedPostIds.contains(post.getId());
                    boolean scrapped = scrappedPostIds.contains(post.getId());

                    return new PostAllResponseDto.Posts(
                            post.getId(),
                            post.getTitle(),
                            new PostAuthorResponseDto(
                                    user.getNicknameCommunity(),
                                    user.getImagePath()
                            ),
                            thumbnailImage,
                            post.getLikeCount(),
                            post.getCommentCount(),
                            post.getViewCount(),
                            post.getCreatedAt(),
                            liked,
                            scrapped
                    );
                })
                .toList();

        return new MyPostResponseDto(postSlice.getSize(), posts,
                size,
                postSlice.hasNext() ? postSlice.getContent().get(postSlice.getNumberOfElements() - 1).getId() : null,
                postSlice.hasNext());
    }



    @Override
    @Transactional
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
    @Transactional
    public void deleteUser(Long userId) {

        User user = userRepository.findById(userId);

        if(!user.isActive())
            throw new CustomException(ErrorCode.USER_DELETED);

        user.updateActive(false);
        user.updateDeletedAt(LocalDateTime.now());

        userRepository.delete(user);
    }

    @Override
    @Transactional
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
    @Transactional
    public void recoverUser(Long userId) {

        User user = userRepository.findById(userId);

        if(user.isActive())
            throw new CustomException(ErrorCode.USER_FORBIDDEN);

        user.updateActive(true);
        user.updateDeletedAt(null);

        userRepository.recovery(user);
    }


}
