package com.kakaotech.ott.ott.like.application.serviceImpl;

import com.kakaotech.ott.ott.admin.application.AdminService;
import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.like.application.service.LikeService;
import com.kakaotech.ott.ott.like.domain.repository.LikeRepository;
import com.kakaotech.ott.ott.like.presentation.dto.request.LikeRequestDto;
import com.kakaotech.ott.ott.post.domain.model.Post;
import com.kakaotech.ott.ott.post.domain.model.PostType;
import com.kakaotech.ott.ott.post.domain.repository.PostRepository;
import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.domain.repository.UserAuthRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class LikeServiceIntegrationTest {

    @Autowired
    private LikeService likeService;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserAuthRepository userAuthRepository;

    @Autowired
    private PostRepository postRepository;

    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        user = userAuthRepository.save(User.createUser("test@naver.com", "tester", "test.img"));
        post = postRepository.save(Post.createPost(user.getId(), PostType.AI, "test_title", "test_content"));
    }

    @Test
    void 게시글에_좋아요_누르면_좋아요_카운트_1증가하고_조회_성공() {

        // given
        LikeRequestDto likeRequestDto = new LikeRequestDto(post.getId());

        // when
        likeService.likePost(user.getId(), likeRequestDto);

        // then
        assertTrue(likeRepository.existsByUserIdAndPostId(user.getId(), post.getId()));

        Post updated = postRepository.findById(post.getId());
        assertEquals(1, updated.getLikeCount());

    }

    @Test
    void 이미_좋아요한_게시글에_중복_좋아요_요청시_예외발생() {

        // given
        LikeRequestDto likeRequestDto = new LikeRequestDto(post.getId());
        likeService.likePost(user.getId(), likeRequestDto);

        // when & then
        CustomException exception = assertThrows(
                CustomException.class,
                () -> likeService.likePost(user.getId(), likeRequestDto)
        );

        assertEquals(ErrorCode.LIKE_ALREADY_EXISTS, exception.getErrorCode());
        assertEquals("이미 좋아요한 게시글입니다.", exception.getMessage());

    }

    @Test
    void 좋아요_취소하면_좋아요_카운트_0으로_감소() {

        // given
        LikeRequestDto likeRequestDto = new LikeRequestDto(post.getId());
        likeService.likePost(user.getId(), likeRequestDto);

        // when
        likeService.unlikePost(user.getId(), likeRequestDto);

        // then
        assertFalse(likeRepository.existsByUserIdAndPostId(user.getId(), post.getId()));

        Post updated = postRepository.findById(post.getId());
        assertEquals(0, updated.getLikeCount());
    }

    @Test
    void 좋아요하지_않은_게시글_취소시_LIKE_NOT_FOUND_예외발생() {

        // given
        LikeRequestDto likeRequestDto = new LikeRequestDto(post.getId());

        // when & then
        CustomException exception = assertThrows(
                CustomException.class,
                () -> likeService.unlikePost(user.getId(), likeRequestDto)
        );

        assertEquals(ErrorCode.LIKE_NOT_FOUND, exception.getErrorCode());
        assertEquals("좋아요하지 않은 게시글입니다.", exception.getMessage());
    }

}
