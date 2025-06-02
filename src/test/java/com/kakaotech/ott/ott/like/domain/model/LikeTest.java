package com.kakaotech.ott.ott.like.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LikeTest {

    @Test
    void POST_타입일_때_게시글_Like_객체가_생성된다() {

        // given
        Long userId = 1L;
        LikeType type = LikeType.POST;
        Long targetId = 1L;

        // when
        Like like = Like.createLike(userId, type, targetId);

        // then
        assertEquals(userId, like.getUserId());
        assertEquals(type, like.getType());
        assertEquals(targetId, like.getTargetId());
        assertTrue(like.getIsActive());
        assertNull(like.getCreatedAt());
    }

    @Test
    void PRODUCT_타입일_때_상품_Like_객체가_생성된다() {

        // given
        Long userId = 1L;
        LikeType type = LikeType.PRODUCT;
        Long targetId = 1L;

        // when
        Like like = Like.createLike(userId, type, targetId);

        // then
        assertEquals(userId, like.getUserId());
        assertEquals(type, like.getType());
        assertEquals(targetId, like.getTargetId());
        assertTrue(like.getIsActive());
        assertNull(like.getCreatedAt());
    }

}