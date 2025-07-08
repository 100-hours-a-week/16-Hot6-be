package com.kakaotech.ott.ott.like.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.like.domain.model.Like;
import com.kakaotech.ott.ott.like.domain.repository.LikeJpaRepository;
import com.kakaotech.ott.ott.like.infrastructure.entity.LikeEntity;
import com.kakaotech.ott.ott.post.domain.repository.PostJpaRepository;
import com.kakaotech.ott.ott.post.infrastructure.entity.PostEntity;
import com.kakaotech.ott.ott.user.domain.repository.UserJpaRepository;
import com.kakaotech.ott.ott.user.infrastructure.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeRepositoryImplTest {

    @Mock
    private LikeJpaRepository likeJpaRepository;

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private PostJpaRepository postJpaRepository;

    @InjectMocks
    private LikeRepositoryImpl likeRepositoryImpl;

    private final Long validUserId = 1L;
    private final Long validPostId = 1L;
    private final Long invalidUserId = 999L;

    private Like like;
    private UserEntity userEntity;
    private PostEntity postEntity;
    private LikeEntity likeEntity;
    private LikeEntity savedLikeEntity;
    private Like expectedLike;

    @BeforeEach
    void setUp() {
        like = mock(Like.class);
        userEntity = mock(UserEntity.class);
        postEntity = mock(PostEntity.class);
        likeEntity = mock(LikeEntity.class);
        savedLikeEntity = mock(LikeEntity.class);
        expectedLike = mock(Like.class);
    }

    @Test
    void 유저가_게시글_좋아요_삭제_요청하면_JpaRepository_메서드_호출됨() {
        // when
        likeRepositoryImpl.deleteByUserEntityIdAndTargetId(validUserId, validPostId);

        // then
        verify(likeJpaRepository, times(1)).deleteByUserEntityIdAndTargetId(validUserId, validPostId);
    }

    @Test
    void 좋아요_저장시_DB에_저장되고_도메인_객체_반환() {
        // given
        when(like.getUserId()).thenReturn(validUserId);
        when(like.getPostId()).thenReturn(validPostId);
        when(userJpaRepository.findById(validUserId)).thenReturn(Optional.of(userEntity));
        when(postJpaRepository.findById(validPostId)).thenReturn(Optional.of(postEntity));

        try (var mockedStatic = org.mockito.Mockito.mockStatic(LikeEntity.class)) {
            mockedStatic.when(() -> LikeEntity.from(like, userEntity, postEntity)).thenReturn(likeEntity);
            when(likeJpaRepository.save(likeEntity)).thenReturn(savedLikeEntity);
            when(savedLikeEntity.toDomain()).thenReturn(expectedLike);

            // when
            Like result = likeRepositoryImpl.save(like);

            // then
            assertEquals(expectedLike, result);
            verify(userJpaRepository, times(1)).findById(validUserId);
            verify(likeJpaRepository, times(1)).save(likeEntity);
            verify(postJpaRepository, times(1)).findById(validPostId);
        }
    }

    @Test
    void 좋아요_저장시_유저가_없으면_USER_NOT_FOUND_예외_반환() {

        // given
        when(userJpaRepository.findById(invalidUserId)).thenReturn(Optional.empty());
        when(like.getUserId()).thenReturn(invalidUserId);

        // when
        CustomException exception = assertThrows(
                CustomException.class,
                () -> likeRepositoryImpl.save(like)
        );

        // then
        verify(userJpaRepository, times(1)).findById(invalidUserId);
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        assertEquals("해당 사용자가 존재하지 않습니다.", exception.getMessage());

    }

    @Test
    void 유저가_게시글에_좋아요를_눌렀을때_true_반환() {

        // given
        when(likeJpaRepository.existsByUserEntityIdAndPostEntityId(validUserId, validPostId)).thenReturn(true);

        // when
        boolean result = likeRepositoryImpl.existsByUserIdAndPostId(validUserId, validPostId);

        // then
        verify(likeJpaRepository, times(1)).existsByUserEntityIdAndPostEntityId(validUserId, validPostId);
        assertTrue(result);
    }

    @Test
    void 유저가_게시글에_좋아요를_누르지_않았을때_false_반환() {

        // given
        when(likeJpaRepository.existsByUserEntityIdAndPostEntityId(validUserId, validPostId)).thenReturn(false);

        // when
        boolean result = likeRepositoryImpl.existsByUserIdAndPostId(validUserId, validPostId);

        // then
        verify(likeJpaRepository, times(1)).existsByUserEntityIdAndPostEntityId(validUserId, validPostId);
        assertFalse(result);
    }

    @Test
    void 게시글_좋아요_수를_정상적으로_조회_성공() {

        // given
        when(likeJpaRepository.countByPostId(validPostId)).thenReturn(5L);

        // when
        Long result = likeRepositoryImpl.findByPostId(validPostId);

        // then
        verify(likeJpaRepository, times(1)).countByPostId(validPostId);

        assertEquals(result, likeJpaRepository.countByPostId(validPostId));
    }

    @Test
    void 유저가_좋아요한_게시글_목록을_Set_타입으로_반환() {

        // given
        List<Long> postIds = Arrays.asList(1L, 2L, 3L);
        List<Long> likedPostIds = Arrays.asList(2L, 3L);

        when(likeJpaRepository.findLikedPostIds(validUserId, postIds)).thenReturn(likedPostIds);

        // when
        Set<Long> result = likeRepositoryImpl.findLikedPostIdsByUserId(validUserId, postIds);

        // then
        verify(likeJpaRepository, times(1)).findLikedPostIds(validUserId, postIds);

        assertEquals(result, new HashSet<>(likeJpaRepository.findLikedPostIds(validUserId, postIds)));
    }
}