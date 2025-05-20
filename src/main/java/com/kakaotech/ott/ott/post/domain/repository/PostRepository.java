package com.kakaotech.ott.ott.post.domain.repository;

import com.kakaotech.ott.ott.post.domain.model.Post;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface PostRepository {

    Post save(Post post);

    Post findById(Long postId);

    void deletePost(Long userId);

    List<Post> findAllByCursor(int size, Long lastPostId, Integer lastLikeCount, Long lastViewCount,
                               String category, String sort);

    Slice<Post> findUserPost(Long userId, Long cursorId, int size);

    void incrementViewCount(Long postId, Long delta);

    void incrementLikeCount(Long postId, Long delta);

    void incrementScrapCount(Long postId, Long delta);

    void incrementCommentCount(Long postId, Long delta);

    List<Post> findTop7ByWeight();

    void batchUpdateWeights();
}
