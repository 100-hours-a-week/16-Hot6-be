package com.kakaotech.ott.ott.like.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaotech.ott.ott.like.config.WithMockCustomUser;
import com.kakaotech.ott.ott.like.presentation.dto.request.LikeRequestDto;
import com.kakaotech.ott.ott.post.domain.repository.PostRepository;
import com.kakaotech.ott.ott.user.domain.model.Role;
import com.kakaotech.ott.ott.user.domain.repository.UserAuthRepository;
import com.kakaotech.ott.ott.user.domain.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class LikeWebMvcControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserAuthRepository userAuthRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    private LikeRequestDto likeRequestDto;
    private final Long postId = 194L;

    @BeforeEach
    void setUp() {
        likeRequestDto = new LikeRequestDto(postId);
    }

    @Test
    @WithMockCustomUser(id = 111L, email = "test@email.com", roles = Role.USER)
    void 로그인_사용자가_게시글_좋아요_요청하면_201_반환() throws Exception {

        // when & then
        mockMvc.perform(post("/api/v1/likes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(likeRequestDto))
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("좋아요 완료"));
    }

    @Test
    void 비로그인_사용자가_게시글_좋아요_요청하면_401_반환() throws Exception {

        // when $ then
        mockMvc.perform(post("/api/v1/likes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(likeRequestDto))
        )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockCustomUser(id = 111L, email = "test@email.com", roles = Role.USER)
    void 로그인_사용자가_게시글_좋아요_취소_요청하면_204_반환() throws Exception {

        // when & then
        mockMvc.perform(post("/api/v1/likes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(likeRequestDto)))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/v1/likes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(likeRequestDto))
        )
                .andExpect(status().isNoContent());


    }

    @Test
    void 비로그인_사용자가_게시글_좋아요_취소_요청하면_401_반환() throws Exception {

        // when & then
        mockMvc.perform(delete("/api/v1/likes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(likeRequestDto))
        )
                .andExpect(status().isUnauthorized());
    }
}