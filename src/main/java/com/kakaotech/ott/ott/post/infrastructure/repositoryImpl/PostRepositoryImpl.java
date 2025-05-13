package com.kakaotech.ott.ott.post.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.post.domain.model.Post;
import com.kakaotech.ott.ott.post.domain.model.PostType;
import com.kakaotech.ott.ott.post.domain.repository.PostJpaRepository;
import com.kakaotech.ott.ott.post.domain.repository.PostRepository;
import com.kakaotech.ott.ott.post.infrastructure.entity.PostEntity;
import com.kakaotech.ott.ott.postImage.domain.PostImage;
import com.kakaotech.ott.ott.user.infrastructure.entity.UserEntity;
import com.kakaotech.ott.ott.user.domain.repository.UserJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {

    private final PostJpaRepository postJpaRepository;
    private final UserJpaRepository userJpaRepository;


    @Override
    public Post save(Post post) {

        UserEntity userEntity = userJpaRepository.findById(post.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("회원가입한 사용자가 아닙니다."));

        // 2) 기존 엔티티 fetch (update vs insert 분기)
        PostEntity postEntity = (post.getId() != null)
                ? postJpaRepository.findById(post.getId()).orElseThrow()
                : new PostEntity();
        postEntity.setUserEntity(userEntity);
        postEntity.setTitle(post.getTitle());
        postEntity.setContent(post.getContent());
        postEntity.setType(post.getType());

        postEntity.getPostImages().clear();
        if (post.getImages() != null) {
            for (PostImage domainImg : post.getImages()) {
                // domainImg.toEntity(entity) 는 FK 세팅된 엔티티 반환
                postEntity.getPostImages().add(domainImg.toEntity(postEntity));
            }
        }

        PostEntity savedPostEntity = postJpaRepository.save(postEntity);

        return savedPostEntity.toDomain();
    }

    @Override
    public Post findById(Long postId) {

        return postJpaRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND))
                .toDomain();
    }

    @Override
    public void deletePost(Long postId) {

        postJpaRepository.deleteById(postId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> findAllByCursor(int size, Long lastPostId, String category, String sort) {
        Pageable pageable = PageRequest.of(0, size);

        if (category == null || "ALL".equalsIgnoreCase(category)) {
            return switch (sort == null ? "LATEST" : sort.toUpperCase()) {
                case "LIKE" -> postJpaRepository.findAllPostsByLike(lastPostId, pageable)
                        .stream().map(PostEntity::toDomain).toList();
                case "VIEW" -> postJpaRepository.findAllPostsByView(lastPostId, pageable)
                        .stream().map(PostEntity::toDomain).toList();
                default -> postJpaRepository.findAllPosts(lastPostId, pageable)
                        .stream().map(PostEntity::toDomain).toList();
            };
        }

        PostType postType = PostType.valueOf(category.toUpperCase());

        return switch (sort == null ? "LATEST" : sort.toUpperCase()) {
            case "LIKE" -> postJpaRepository.findByCategoryByLike(postType, lastPostId, pageable)
                    .stream().map(PostEntity::toDomain).toList();
            case "VIEW" -> postJpaRepository.findByCategoryByView(postType, lastPostId, pageable)
                    .stream().map(PostEntity::toDomain).toList();
            default -> postJpaRepository.findByCategory(postType, lastPostId, pageable)
                    .stream().map(PostEntity::toDomain).toList();
        };
    }

    @Override
    @Transactional
    public void incrementViewCount(Long postId, Long delta) {

        postJpaRepository.incrementViewCount(postId, delta);
    }

    @Override
    @Transactional
    public void incrementLikeCount(Long postId, Long delta) {

        postJpaRepository.incrementLikeCount(postId, delta);
    }

    @Override
    @Transactional
    public void incrementScrapCount(Long postId, Long delta) {

        postJpaRepository.incrementScrapCount(postId, delta);
    }

    @Override
    @Transactional
    public void incrementCommentCount(Long postId, Long delta) {

        postJpaRepository.incrementCommentCount(postId, delta);
    }

    @Override
    public List<Post> findTop7ByWeight() {
        // DB에서 직접 weight 기준 상위 7개 조회 + AI 이미지 JOIN
        List<PostEntity> entities = postJpaRepository.findTop7ByTypeOrderByWeightDescWithAiImages(PageRequest.of(0, 7));

        // Entity → Domain 변환
        return entities.stream()
                .map(PostEntity::toDomain)
                .collect(Collectors.toList());
    }
}
