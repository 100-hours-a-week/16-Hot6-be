package com.kakaotech.ott.ott.post.application.serviceImpl;

import com.kakaotech.ott.ott.aiImage.application.serviceImpl.S3Uploader;
import com.kakaotech.ott.ott.aiImage.domain.model.AiImage;
import com.kakaotech.ott.ott.aiImage.domain.repository.AiImageRepository;
import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.like.domain.repository.LikeRepository;
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
import com.kakaotech.ott.ott.user.infrastructure.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    @Value("${cloud.aws.s3.base-url}")
    private String baseUrl;

    @Override
    @Transactional
    public PostCreateResponseDto createFreePost(FreePostCreateRequestDto freePostCreateRequestDto, Long userId)
            throws IOException {

        Post post = Post.createPost(userId, PostType.FREE,
                freePostCreateRequestDto.getTitle(), freePostCreateRequestDto.getContent());

        UserEntity userEntity = userAuthRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        int seq = 1;
        for (MultipartFile file : freePostCreateRequestDto.getImages()) {
            String url = baseUrl + s3Uploader.upload(file);
            
            post.addImage(PostImage.createPostImage(post.getId(), seq++, url));
        }

        Post savedPost = postRepository.save(post);

        return new PostCreateResponseDto(savedPost.getId());
    }

    @Override
    @Transactional
    public PostCreateResponseDto createAiPost(AiPostCreateRequestDto aiPostCreateRequestDto, Long userId) {

        Post post = Post.createPost(userId, PostType.AI,
                aiPostCreateRequestDto.getTitle(), aiPostCreateRequestDto.getContent());

        User user = userAuthRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND))
                .toDomain();

        user.updatePoint(500);
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
    @Transactional
    public PostAllResponseDto getAllPost(Long userId, int size, Long lastPostId) {

        if (lastPostId != null && lastPostId <= 0) {
            throw new CustomException(ErrorCode.INVALID_CURSOR);
        }

        // 1) 커서 기반으로 size 개만 가져오기
        List<Post> posts = postRepository.findAllByCursor(size, lastPostId);

        // 2) 엔티티 → DTO 매핑
        List<PostAllResponseDto.Posts> dtoList = posts.stream()
                .map(post -> {
                    UserEntity author = userAuthRepository.findById(post.getUserId())
                            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

                    boolean liked = likeRepository.existsByUserIdAndPostId(userId, post.getId());
                    boolean scrapped = scrapRepository.existsByUserIdAndTypeAndPostId(userId, ScrapType.POST, post.getId());

                    String thumbnailImage = switch (post.getType()) {
                        case AI -> aiImageRepository.findByPostId(post.getId()).getAfterImagePath();
                        case FREE -> post.getImages().get(0).getImageUuid();
                    };

                    return new PostAllResponseDto.Posts(
                            post.getId(),
                            post.getTitle(),
                            new PostAuthorResponseDto(
                                    author.getNicknameCommunity(),
                                    author.getImagePath()
                            ),
                            thumbnailImage,
                            post.getLikeCount(),
                            post.getCommentCount(),
                            post.getCreatedAt(),
                            liked,
                            scrapped
                    );
                })
                .toList();

        // 3) 다음 페이지 유무 및 커서 계산
        boolean hasNext = dtoList.size() == size;
        Long nextLastId = hasNext
                ? dtoList.get(dtoList.size() - 1).getPostId()
                : null;

        PostAllResponseDto.Pagination pagination =
                new PostAllResponseDto.Pagination(size, nextLastId, hasNext);

        return new PostAllResponseDto(dtoList, pagination);

    }

    @Override
    @Transactional
    public PostGetResponseDto getPost(Long postId, Long userId) {

        viewCountAggregator.increment(postId);

        Post post = postRepository.findById(postId);

        User user = userAuthRepository.findById(post.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND))
                .toDomain();

        boolean isOwner = post.getUserId().equals(userId);
        boolean liked = likeRepository.existsByUserIdAndPostId(userId, post.getId());
        boolean scrapped = scrapRepository.existsByUserIdAndTypeAndPostId(userId, ScrapType.POST, post.getId());

        List<?> imageUrls = imageLoaderManager.loadImages(post.getType(), postId);

        return new PostGetResponseDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getType(),
                new PostAuthorResponseDto(user.getNicknameCommunity(), user.getImagePath()),
                post.getLikeCount(),
                post.getCommentCount(),
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

        UserEntity userEntity = userAuthRepository.findById(userId)
                .orElseThrow();

        Post post = postRepository.findById(postId);

        if(!post.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.USER_FORBIDDEN);
        }

        for (PostImage img : post.getImages()) {
            s3Uploader.delete(img.getImageUuid());
        }

        postRepository.deletePost(postId);
    }

    @Override
    @Transactional
    public PostCreateResponseDto updateFreePost(Long postId, Long userId, FreePostUpdateRequestDto freePostUpdateRequestDto) throws IOException {

        User user = userAuthRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND))
                .toDomain();

        Post post = postRepository.findById(postId);

        if (!post.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.USER_FORBIDDEN);
        }
        // 2) 변경 가능한 필드 적용
        if (freePostUpdateRequestDto.getTitle() != null) post.updateTitle(freePostUpdateRequestDto.getTitle());
        if (freePostUpdateRequestDto.getContent() != null) post.updateContent(freePostUpdateRequestDto.getContent());
        // 3) 이미지 교체 로직
        if (freePostUpdateRequestDto.getImages() != null) {

            for (PostImage img : post.getImages()) {
                s3Uploader.delete(img.getImageUuid());
            }

            // 기존 이미지 전부 삭제
            post.clearImages();

            int seq = 1;
            for (MultipartFile file : freePostUpdateRequestDto.getImages()) {
                String url = baseUrl + s3Uploader.upload(file);
                System.out.println(url);
                post.addImage(PostImage.createPostImage(post.getId(), seq++, url));
            }
        }

        Post savedPost = postRepository.save(post);
        return new PostCreateResponseDto(savedPost.getId());
    }

    @Override
    public PostCreateResponseDto updateAiPost(Long postId, Long userId, AiPostUpdateRequestDto aiPostUpdateRequestDto) throws IOException {
        User user = userAuthRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND))
                .toDomain();
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

            if(aiImage.getPostId() != null) {
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
    public List<PopularSetupDto> getPopularSetups(Long userId) {
        // 상위 7개 게시글 조회 (단일 조회)
        List<Post> popularPosts = postRepository.findTop7ByWeight();
        List<Long> postIds = popularPosts.stream().map(Post::getId).collect(Collectors.toList());

        // AI 이미지 Batch 조회 (Domain Repository)
        Map<Long, AiImage> aiImageMap = aiImageRepository.findByPostIds(postIds);

        // Scrap 여부 Batch 조회 (Domain Repository)
        Set<Long> scrappedPostIds = scrapRepository.findScrappedPostIds(userId, postIds);

        // PopularSetupDto 생성
        return popularPosts.stream()
                .map(post -> new PopularSetupDto(
                        post.getId(),
                        post.getTitle(),
                        aiImageMap.getOrDefault(post.getId(), null) != null ?
                                aiImageMap.get(post.getId()).getAfterImagePath() : "",
                        scrappedPostIds.contains(post.getId())
                ))
                .collect(Collectors.toList());
    }


}
