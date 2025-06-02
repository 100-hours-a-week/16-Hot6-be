package com.kakaotech.ott.ott.like.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("좋아요에 대한 테스트")
class LikeTest {

    @Test
    @DisplayName("LikeType이 POST일 때 게시글에 대한 Like 객체가 만들어진다.")
    void should_return_post_like_when_create_post_type_like() {

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
    @DisplayName("LikeType이 PRODUCT일 때 상품에 대한 Like 객체가 만들어진다.")
    void should_return_product_like_when_create_product_type_like() {

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