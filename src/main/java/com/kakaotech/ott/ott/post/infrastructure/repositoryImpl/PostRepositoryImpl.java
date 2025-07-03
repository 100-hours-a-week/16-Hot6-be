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
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

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
    public List<Post> findAllByCursor(int size, Long lastPostId, Integer lastLikeCount, Long lastViewCount, Double lastWeightCount,
                                      String category, String sort) {
        Pageable pageable = PageRequest.of(0, size);

        if (category == null || "ALL".equalsIgnoreCase(category)) {
            return switch (sort == null ? "LATEST" : sort.toUpperCase()) {
                case "LIKE" -> {
                    if (lastPostId == null || lastLikeCount == null) {
                        yield postJpaRepository.findAllPostsByLike(pageable)
                                .stream().map(PostEntity::toDomain).toList();
                    } else {
                        yield postJpaRepository.findAllPostsByLike(lastLikeCount, lastPostId, pageable)
                                .stream().map(PostEntity::toDomain).toList();
                    }
                }
                case "VIEW" -> {
                    if (lastPostId == null || lastViewCount == null) {
                        yield postJpaRepository.findAllPostsByView(pageable)
                                .stream().map(PostEntity::toDomain).toList();
                    } else {
                        yield postJpaRepository.findAllPostsByView(lastViewCount, lastPostId, pageable)
                                .stream().map(PostEntity::toDomain).toList();
                    }
                }
                default -> {
                    if (lastPostId == null) {
                        yield postJpaRepository.findAllPosts(pageable).stream().map(PostEntity::toDomain).toList();
                    } else {
                        yield postJpaRepository.findAllPosts(lastPostId, pageable)
                                .stream().map(PostEntity::toDomain).toList();
                    }
                }

            };
        }

        PostType postType = PostType.valueOf(category.toUpperCase());

        return switch (sort == null ? "LATEST" : sort.toUpperCase()) {
            case "LIKE" -> {
                if (lastPostId == null || lastLikeCount == null) {
                    yield postJpaRepository.findByCategoryByLike(postType, pageable).stream().map(PostEntity::toDomain).toList();
                } else {
                    yield postJpaRepository.findByCategoryByLike(postType, lastLikeCount, lastPostId, pageable)
                            .stream().map(PostEntity::toDomain).toList();
                }
            }
            case "VIEW" -> {
                if (lastPostId == null || lastViewCount == null) {
                    yield postJpaRepository.findByCategoryByView(postType, pageable).stream().map(PostEntity::toDomain).toList();
                } else {
                    yield postJpaRepository.findByCategoryByView(postType, lastViewCount, lastPostId, pageable)
                            .stream().map(PostEntity::toDomain).toList();
                }
            }
            case "POPULAR" -> {
                if (lastPostId == null || lastWeightCount == null) {
                    yield postJpaRepository.findByCategoryByWeight(postType, pageable).stream().map(PostEntity::toDomain).toList();
                } else {
                    yield postJpaRepository.findByCategoryByWeight(postType, lastWeightCount, lastPostId, pageable)
                            .stream().map(PostEntity::toDomain).toList();
                }
            }
            default -> {
                if (lastPostId == null) {
                    yield postJpaRepository.findByCategory(postType, pageable).stream().map(PostEntity::toDomain).toList();
                } else {
                    yield postJpaRepository.findByCategory(postType, lastPostId, pageable)
                            .stream().map(PostEntity::toDomain).toList();
                }
            }
        };
    }

    @Override
    public Slice<Post> findUserPost(Long userId, Long cursorId, int size) {

        Slice<PostEntity> slice = postJpaRepository.findUserAllPosts(
                userId, cursorId, PageRequest.of(0, size)
        );

        return slice.map(PostEntity::toDomain);
    }

    @Override
    public void incrementViewCount(Long postId, Long delta) {

        PostEntity postEntity = postJpaRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if(postEntity.getViewCount() + delta < 0)
            return;

        postJpaRepository.incrementViewCount(postId, delta);
    }

    @Override
    public void incrementLikeCount(Long postId, Long delta) {

        PostEntity postEntity = postJpaRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if(postEntity.getLikeCount() + delta < 0)
            return;

        postJpaRepository.incrementLikeCount(postId, delta);
    }

    @Override
    public void incrementScrapCount(Long postId, Long delta) {

        PostEntity postEntity = postJpaRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if(postEntity.getScrapCount() + delta < 0)
            return;

        postJpaRepository.incrementScrapCount(postId, delta);
    }

    @Override
    public void incrementCommentCount(Long postId, Long delta) {

        PostEntity postEntity = postJpaRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if(postEntity.getCommentCount() + delta < 0)
            return;

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

    @Override
    public void batchUpdateWeights() {
        postJpaRepository.batchUpdateWeights();
    }


}
