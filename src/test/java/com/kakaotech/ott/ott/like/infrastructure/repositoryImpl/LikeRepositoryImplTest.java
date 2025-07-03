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

    @Test
    void 유저가_게시글_좋아요_삭제_요청하면_JpaRepository_메서드_호출됨() {

        // given
        Long userId = 1L;
        Long postId = 1L;

        // when
        likeRepositoryImpl.deleteByUserEntityIdAndTargetId(userId, postId);

        // then
        verify(likeJpaRepository, times(1)).deleteByUserEntityIdAndTargetId(userId, postId);
    }

    @Test
    void 좋아요_저장시_DB에_저장되고_도메인_객체_반환() {

        // given
        Like like = mock(Like.class);
        UserEntity userEntity = mock(UserEntity.class);
        PostEntity postEntity = mock(PostEntity.class);
        LikeEntity likeEntity = mock(LikeEntity.class);
        LikeEntity savedEntity = mock(LikeEntity.class);
        Like expectedLike = mock(Like.class);

        when(like.getUserId()).thenReturn(1L);
        when(like.getPostId()).thenReturn(1L);
        when(userJpaRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(postJpaRepository.findById(1L)).thenReturn(Optional.of(postEntity));

        try (var mockedStatic = org.mockito.Mockito.mockStatic(LikeEntity.class)) {
            mockedStatic.when(() -> LikeEntity.from(like, userEntity, postEntity)).thenReturn(likeEntity);

            when(likeJpaRepository.save(likeEntity)).thenReturn(savedEntity);

            when(savedEntity.toDomain()).thenReturn(expectedLike);

            // when
            Like result = likeRepositoryImpl.save(like);

            // then
            verify(userJpaRepository, times(1)).findById(1L);
            verify(likeJpaRepository, times(1)).save(likeEntity);

            assertEquals(result, savedEntity.toDomain());
        }
    }

    @Test
    void 좋아요_저장시_유저가_없으면_USER_NOT_FOUND_예외_반환() {

        // given
        Like like = mock(Like.class);

        when(like.getUserId()).thenReturn(-999L);

        // when
        CustomException exception = assertThrows(
                CustomException.class,
                () -> likeRepositoryImpl.save(like)
        );

        // then
        verify(userJpaRepository, times(1)).findById(like.getUserId());

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        assertEquals("해당 사용자가 존재하지 않습니다.", exception.getMessage());

    }

    @Test
    void 유저가_게시글에_좋아요를_눌렀을때_true_반환() {

        // given
        Long userId = 1L;
        Long postId = 1L;

        when(likeJpaRepository.existsByUserEntityIdAndPostEntityId(userId, postId)).thenReturn(true);

        // when
        boolean result = likeRepositoryImpl.existsByUserIdAndPostId(userId, postId);

        // then
        verify(likeJpaRepository, times(1)).existsByUserEntityIdAndPostEntityId(userId, postId);
        assertTrue(result);
    }

    @Test
    void 유저가_게시글에_좋아요를_누르지_않았을때_false_반환() {

        // given
        Long userId = 1L;
        Long postId = 1L;

        when(likeJpaRepository.existsByUserEntityIdAndPostEntityId(userId, postId)).thenReturn(false);

        // when
        boolean result = likeRepositoryImpl.existsByUserIdAndPostId(userId, postId);

        // then
        verify(likeJpaRepository, times(1)).existsByUserEntityIdAndPostEntityId(userId, postId);
        assertFalse(result);
    }

    @Test
    void 게시글_좋아요_수를_정상적으로_조회_성공() {

        // given
        Long postId = 1L;

        when(likeJpaRepository.countByPostId(postId)).thenReturn(5L);

        // when
        Long result = likeRepositoryImpl.findByPostId(postId);

        // then
        verify(likeJpaRepository, times(1)).countByPostId(postId);

        assertEquals(result, likeJpaRepository.countByPostId(postId));
    }

    @Test
    void 유저가_좋아요한_게시글_목록을_Set_타입으로_반환() {

        // given
        Long userid = 1L;
        List<Long> postIds = Arrays.asList(1L, 2L, 3L);
        List<Long> likedPostIds = Arrays.asList(2L, 3L);

        when(likeJpaRepository.findLikedPostIds(userid, postIds)).thenReturn(likedPostIds);

        // when
        Set<Long> result = likeRepositoryImpl.findLikedPostIdsByUserId(userid, postIds);

        // then
        verify(likeJpaRepository, times(1)).findLikedPostIds(userid, postIds);

        assertEquals(result, new HashSet<>(likeJpaRepository.findLikedPostIds(userid, postIds)));
    }
}