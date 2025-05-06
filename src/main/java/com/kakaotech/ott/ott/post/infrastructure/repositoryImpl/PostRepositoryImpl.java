package com.kakaotech.ott.ott.post.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.post.domain.model.Post;
import com.kakaotech.ott.ott.post.domain.repository.PostJpaRepository;
import com.kakaotech.ott.ott.post.domain.repository.PostRepository;
import com.kakaotech.ott.ott.post.infrastructure.entity.PostEntity;
import com.kakaotech.ott.ott.postImage.domain.PostImage;
import com.kakaotech.ott.ott.user.infrastructure.entity.UserEntity;
import com.kakaotech.ott.ott.user.infrastructure.repository.UserJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


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
    public List<Post> findAllByCursor(int size, Long lastPostId) {
        Pageable pg = PageRequest.of(0, size, Sort.by("id").descending());
        Page<PostEntity> page;
        if (lastPostId == null) {
            page = postJpaRepository.findAllByOrderByIdDesc(pg);
        } else {
            page = postJpaRepository.findAllByIdLessThanOrderByIdDesc(lastPostId, pg);
        }
        return page.stream()
                .map(PostEntity::toDomain)
                .toList();
    }

    @Override
    public Post findById(Long postId) {

        return postJpaRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("해당 게시물이 존재하지 않습니다."))
                .toDomain();
    }

    @Override
    public void deletePost(Long postId) {

        postJpaRepository.deleteById(postId);
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
}
