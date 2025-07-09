package com.kakaotech.ott.ott.like.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LikeTest {

    private final Long userId = 1L;
    private final Long postId = 1L;

    private Like like;

    @BeforeEach
    void setUp() {
        like = Like.createLike(userId, postId);
    }

    @Test
    void Like_객체가_생성된다() {
        // then
        assertEquals(userId, like.getUserId());
        assertEquals(postId, like.getPostId());
        assertTrue(like.getIsActive());
        assertNull(like.getCreatedAt());
    }


}