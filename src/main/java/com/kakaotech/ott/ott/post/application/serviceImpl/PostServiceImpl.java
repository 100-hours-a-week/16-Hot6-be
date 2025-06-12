package com.kakaotech.ott.ott.post.application.serviceImpl;

import com.kakaotech.ott.ott.aiImage.application.serviceImpl.S3Uploader;
import com.kakaotech.ott.ott.aiImage.domain.model.AiImage;
import com.kakaotech.ott.ott.aiImage.domain.repository.AiImageRepository;
import com.kakaotech.ott.ott.comment.domain.repository.CommentRepository;
import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.like.domain.repository.LikeRepository;
import com.kakaotech.ott.ott.pointHistory.domain.model.PointActionReason;
import com.kakaotech.ott.ott.pointHistory.domain.model.PointActionType;
import com.kakaotech.ott.ott.pointHistory.domain.model.PointHistory;
import com.kakaotech.ott.ott.pointHistory.domain.repository.PointHistoryRepository;
import com.kakaotech.ott.ott.post.application.component.ImageLoaderManager;
import com.kakaotech.ott.ott.post.application.component.ViewCountAggregator;
import com.kakaotech.ott.ott.post.application.service.PostService;
import com.kakaotech.ott.ott.post.domain.model.Post;
import com.kakaotech.ott.ott.post.domain.model.PostType;
import com.kakaotech.ott.ott.post.domain.repository.PostRepository;
import com.kakaotech.ott.ott.post.presentation.dto.request.AiPostCreateRequestDto;
import com.kakaotech.ott.ott.post.presentation.dto.request.AiPostUpdateRequestDto;
import com.kakaotech.ott.ott.post.presentation.dto.request.FreePostUpdateRequestDto;
import com.kakaotech.ott.ott.post.presentation.dto.response.*;
import com.kakaotech.ott.ott.post.presentation.dto.request.FreePostCreateRequestDto;
import com.kakaotech.ott.ott.postImage.domain.PostImage;
import com.kakaotech.ott.ott.scrap.domain.model.ScrapType;
import com.kakaotech.ott.ott.scrap.domain.repository.ScrapRepository;
import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.domain.repository.UserAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserAuthRepository userAuthRepository;
    private final AiImageRepository aiImageRepository;
    private final S3Uploader s3Uploader;
    private final ViewCountAggregator viewCountAggregator;
    private final LikeRepository likeRepository;
    private final ScrapRepository scrapRepository;
    private final ImageLoaderManager imageLoaderManager;
    private final CommentRepository commentRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Value("${cloud.aws.s3.base-url}")
    private String baseUrl;

    @Value("${cloud.aws.s3.basic-profile}")
    private String basicProfile;

    @Override
    @Transactional
    public PostCreateResponseDto createFreePost(FreePostCreateRequestDto freePostCreateRequestDto, Long userId)
            throws IOException {

        Post post = Post.createPost(userId, PostType.FREE,
                freePostCreateRequestDto.getTitle(), freePostCreateRequestDto.getContent());

        User user = userAuthRepository.findById(userId);

        if (freePostCreateRequestDto.getImages() != null) {
            int seq = 1;
            for (MultipartFile file : freePostCreateRequestDto.getImages()) {
                String url = baseUrl + s3Uploader.upload(file);

                post.addImage(PostImage.createPostImage(post.getId(), seq++, url));
            }
        }

        Post savedPost = postRepository.save(post);

        return new PostCreateResponseDto(savedPost.getId());
    }

    @Override
    @Transactional
    public PostCreateResponseDto createAiPost(AiPostCreateRequestDto aiPostCreateRequestDto, Long userId) {

        Post post = Post.createPost(userId, PostType.AI,
                aiPostCreateRequestDto.getTitle(), aiPostCreateRequestDto.getContent());

        User user = userAuthRepository.findById(userId);

        PointHistory beforePointHistory = pointHistoryRepository.findLatestPointHistoryByUserId(user.getId());

        PointHistory afterPointHistory = PointHistory.createPointHistory(user.getId(), 200, beforePointHistory.getBalanceAfter() + 200, PointActionType.EARN, PointActionReason.POST_CREATE);
        pointHistoryRepository.save(afterPointHistory, user);
        // user.updatePoint(200); TODO: PointHistory로 변경
        userAuthRepository.save(user);

        Post savedPost = postRepository.save(post);

        AiImage aiImage = aiImageRepository.findById(aiPostCreateRequestDto.getAiImageId())
                .orElseThrow(() -> new CustomException(ErrorCode.AIIMAGE_NOT_FOUND))
                .toDomain();

        if(aiImage.getPostId() != null) {
            throw new CustomException(ErrorCode.AI_IMAGE_ALREADY_USED);
        }

        aiImage.updatePostId(savedPost.getId());
        aiImageRepository.savePost(aiImage);

        return new PostCreateResponseDto(savedPost.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public PostAllResponseDto getAllPost(Long userId, String category, String sort, int size, Long lastPostId,
                                         Integer lastLikeCount, Long lastViewCount) {
        List<Post> posts = postRepository.findAllByCursor(size, lastPostId, lastLikeCount, lastViewCount, category, sort);

        List<PostAllResponseDto.Posts> dtoList = posts.stream()
                .map(post -> {
                    User author = userAuthRepository.findById(post.getUserId());

                    boolean liked = likeRepository.existsByUserIdAndPostId(userId, post.getId());
                    boolean scrapped = (userId != null) && scrapRepository.existsByUserIdAndTypeAndPostId(userId, ScrapType.POST, post.getId());
                    int commentCount = commentRepository.findByPostId(post.getId());
                    int likeCount = likeRepository.findByPostId(post.getId());

                    String thumbnailImage = switch (post.getType()) {
                        case AI -> aiImageRepository.findByPostId(post.getId()).getAfterImagePath();
                        case FREE -> post.getImages().isEmpty() ? "" : post.getImages().get(0).getImageUuid();
                    };

                    boolean isActive = userAuthRepository.findById(post.getUserId()).isActive();

                    return new PostAllResponseDto.Posts(
                            post.getId(),
                            post.getTitle(),
                            new PostAuthorResponseDto(isActive
                                    ? author.getNicknameCommunity()
                                    : "알 수 없음",
                                    isActive
                                    ? author.getImagePath()
                                    : basicProfile),
                            thumbnailImage,
                            likeCount,
                            commentCount,
                            post.getViewCount(),
                            post.getCreatedAt(),
                            liked,
                            scrapped
                    );
                })
                .toList();


        boolean hasNext = dtoList.size() == size;
        Long nextLastId = hasNext ? dtoList.get(dtoList.size() - 1).getPostId() : null;
        Integer nextLastLikeCount = hasNext ? dtoList.get(dtoList.size() - 1).getLikeCount() : null;
        Long nextLastViewCount = hasNext ? dtoList.get(dtoList.size() - 1).getViewCount() : null;

        return new PostAllResponseDto(dtoList, new PostAllResponseDto.Pagination(size, nextLastId, nextLastLikeCount, nextLastViewCount, hasNext));
    }

    @Override
    @Transactional(readOnly = true)
    public PostGetResponseDto getPost(Long postId, Long userId) {

        viewCountAggregator.increment(postId);

        Post post = postRepository.findById(postId);

        User user = userAuthRepository.findById(post.getUserId());

        boolean isOwner = post.getUserId().equals(userId);
        boolean liked = likeRepository.existsByUserIdAndPostId(userId, post.getId());
        boolean scrapped = (userId != null) && scrapRepository.existsByUserIdAndTypeAndPostId(userId, ScrapType.POST, post.getId());

        List<?> imageUrls = imageLoaderManager.loadImages(post.getType(), postId);

        int commentCount = commentRepository.findByPostId(post.getId());
        int likeCount = likeRepository.findByPostId(post.getId());

        boolean isActive = userAuthRepository.findById(post.getUserId()).isActive();

        return new PostGetResponseDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getType(),
                new PostAuthorResponseDto(isActive
                        ? user.getNicknameCommunity()
                        : "알 수 없음",
                        isActive
                                ? user.getImagePath()
                                : basicProfile),
                likeCount,
                commentCount,
                post.getViewCount(),
                scrapped,
                liked,
                isOwner,
                imageUrls,
                post.getCreatedAt()
        );
    }

    @Override
    @Transactional
    public void deletePost(Long userId, Long postId) throws AccessDeniedException {

        User user = userAuthRepository.findById(userId);

        Post post = postRepository.findById(postId);

        if(!post.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.USER_FORBIDDEN);
        }

        if(post.getType().equals(PostType.FREE)) {
            for (PostImage img : post.getImages()) {
                s3Uploader.delete(img.getImageUuid());
            }
        } else {
            AiImage aiImage = aiImageRepository.findByPostId(postId);
            aiImage.updatePostId(null);
            aiImageRepository.updatePostId(aiImage);
        }

        postRepository.deletePost(postId);
    }

    @Override
    @Transactional
    public PostCreateResponseDto updateFreePost(Long postId, Long userId, FreePostUpdateRequestDto freePostUpdateRequestDto) throws IOException {

        User user = userAuthRepository.findById(userId);

        Post post = postRepository.findById(postId);

        if (!post.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.USER_FORBIDDEN);
        }
        // 2) 변경 가능한 필드 적용
        if (freePostUpdateRequestDto.getTitle() != null) post.updateTitle(freePostUpdateRequestDto.getTitle());
        if (freePostUpdateRequestDto.getContent() != null) post.updateContent(freePostUpdateRequestDto.getContent());

        // 이미지 목록 처리
        List<PostImage> updatedImages = new ArrayList<>();
        List<PostImage> currentImages = post.getImages() != null ? post.getImages() : new ArrayList<>(); // 기존 이미지가 없을 수 있음

        // 1. 기존 이미지 유지 (Sequence 기반)
        if (freePostUpdateRequestDto.getExistingImageIds() != null && !freePostUpdateRequestDto.getExistingImageIds().isEmpty()) {
            List<Integer> existingSequences = freePostUpdateRequestDto.getExistingImageIds().stream()
                    .map(Integer::parseInt) // 문자열 -> 정수 변환
                    .collect(Collectors.toList());

            // 기존 이미지 중 유지할 이미지 선별 (시퀀스 기준)
            for (Integer sequence : existingSequences) {
                currentImages.stream()
                        .filter(img -> img.getSequence() == sequence) // 시퀀스 값으로 비교
                        .findFirst()
                        .ifPresent(updatedImages::add);
            }
        }

        // 2. 새로 추가할 이미지 처리 (MultipartFile)
        if (freePostUpdateRequestDto.getImages() != null && !freePostUpdateRequestDto.getImages().isEmpty()) {
            int seq = updatedImages.size() + 1;
            for (MultipartFile file : freePostUpdateRequestDto.getImages()) {
                String url = baseUrl + s3Uploader.upload(file);
                updatedImages.add(PostImage.createPostImage(post.getId(), seq++, url));
            }
        }

        // 3. 기존 이미지 중 제거된 이미지 삭제 (S3에서 제거)
        if (!currentImages.isEmpty()) {
            for (PostImage img : currentImages) {
                if (updatedImages.stream().noneMatch(updated -> updated.getSequence() == img.getSequence())) {
                    s3Uploader.delete(img.getImageUuid());
                }
            }
        }

        // 4. 게시글 이미지 목록 갱신
        post.clearImages(); // 기존 이미지 모두 제거

        // 5. 시퀀스 값 다시 설정 후 저장
        if (!updatedImages.isEmpty()) {
            int seq = 1;
            for (PostImage img : updatedImages) {
                img.setSequence(seq++); // 시퀀스를 순차적으로 다시 설정
                post.addImage(img);
            }
        }

        // 저장
        Post savedPost = postRepository.save(post);
        return new PostCreateResponseDto(savedPost.getId());
    }

    @Override
    public PostCreateResponseDto updateAiPost(Long postId, Long userId, AiPostUpdateRequestDto aiPostUpdateRequestDto) throws IOException {
        User user = userAuthRepository.findById(userId);
        Post post = postRepository.findById(postId);

        if (!post.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.USER_FORBIDDEN);
        }
        // 2) 변경 가능한 필드 적용
        if (aiPostUpdateRequestDto.getTitle() != null) post.updateTitle(aiPostUpdateRequestDto.getTitle());
        if (aiPostUpdateRequestDto.getContent() != null) post.updateContent(aiPostUpdateRequestDto.getContent());
        // 3) 이미지 교체 로직
        if (aiPostUpdateRequestDto.getAiImageId() != null) {
            AiImage aiImage = aiImageRepository.findById(aiPostUpdateRequestDto.getAiImageId())
                    .orElseThrow(() -> new CustomException(ErrorCode.AIIMAGE_NOT_FOUND))
                    .toDomain();

            if(aiImage.getPostId() != null && !aiImage.getPostId().equals(post.getId())) {
                throw new CustomException(ErrorCode.AI_IMAGE_ALREADY_USED);
            }

            AiImage beforeAiImage = aiImageRepository.findByPostId(post.getId());
            beforeAiImage.updatePostId(null);

            aiImage.updatePostId(post.getId());

            aiImageRepository.savePost(beforeAiImage);
            aiImageRepository.savePost(aiImage);
        }


        Post savedPost = postRepository.save(post);
        return new PostCreateResponseDto(savedPost.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PopularSetupDto> getPopularSetups(Long userId) {
        // 상위 7개 게시글 조회 (단일 조회)
        List<Post> popularPosts = postRepository.findTop7ByWeight();
        List<Long> postIds = popularPosts.stream().map(Post::getId).collect(Collectors.toList());

        // AI 이미지 Batch 조회 (Domain Repository)
        Map<Long, AiImage> aiImageMap = aiImageRepository.findByPostIds(postIds);

        // Scrap 여부 Batch 조회 (Domain Repository)
        Set<Long> scrappedPostIds = (userId != null)
                ? new HashSet<>(scrapRepository.findScrappedPostIds(userId, postIds))
                : Collections.emptySet();

        // PopularSetupDto 생성
        return popularPosts.stream()
                .map(post -> new PopularSetupDto(
                        post.getId(),
                        post.getTitle(),
                        Optional.ofNullable(aiImageMap.get(post.getId()))
                                .map(AiImage::getAfterImagePath)
                                .orElse(""),
                        (userId != null) && scrappedPostIds.contains(post.getId())
                ))
                .collect(Collectors.toList());
    }


}