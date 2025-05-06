package com.kakaotech.ott.ott.post.domain.repository;

import com.kakaotech.ott.ott.post.domain.model.Post;

import java.util.List;

public interface PostRepository {

    Post save(Post post);

    Post findById(Long postId);

    void deletePost(Long userId);

    List<Post> findAllByCursor(int size, Long lastPostId);

    void incrementViewCount(Long postId, Long delta);

    void incrementLikeCount(Long postId, Long delta);

    void incrementScrapCount(Long postId, Long delta);
}
