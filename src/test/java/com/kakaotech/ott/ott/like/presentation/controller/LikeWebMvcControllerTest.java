package com.kakaotech.ott.ott.like.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaotech.ott.ott.global.config.JpaConfig;
import com.kakaotech.ott.ott.global.security.SecurityConfig;
import com.kakaotech.ott.ott.like.config.WithMockCustomUser;
import com.kakaotech.ott.ott.like.application.service.LikeService;
import com.kakaotech.ott.ott.like.presentation.dto.request.LikeRequestDto;
import com.kakaotech.ott.ott.user.application.serviceImpl.CustomOAuth2UserService;
import com.kakaotech.ott.ott.user.application.serviceImpl.CustomUserDetailsService;
import com.kakaotech.ott.ott.user.application.serviceImpl.JwtService;
import com.kakaotech.ott.ott.user.presentation.controller.OAuth2FailureHandler;
import com.kakaotech.ott.ott.user.presentation.controller.OAuth2SuccessHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = LikeController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JpaConfig.class)
        }
)
@Import(SecurityConfig.class)
public class LikeWebMvcControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LikeService likeService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomOAuth2UserService customOAuth2UserService;
    @MockBean
    private OAuth2SuccessHandler oAuth2SuccessHandler;
    @MockBean
    private OAuth2FailureHandler oAuth2FailureHandler;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockCustomUser(id = 100L, email = "test@naver.com", roles = "USER")
    void 로그인_사용자가_게시글_좋아요_요청하면_201_반환() throws Exception {

        // given
        LikeRequestDto likeRequestDto = new LikeRequestDto(1L);

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

        // given
        LikeRequestDto likeRequestDto = new LikeRequestDto(1L);

        // when $ then
        mockMvc.perform(post("/api/v1/likes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(likeRequestDto))
        )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockCustomUser(id = 100L, email = "test@naver.com", roles = "USER")
    void 로그인_사용자가_게시글_좋아요_취소_요청하면_204_반환() throws Exception {

        // given
        LikeRequestDto likeRequestDto = new LikeRequestDto(1L);

        // when & then
        mockMvc.perform(delete("/api/v1/likes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(likeRequestDto))
        )
                .andExpect(status().isNoContent());


    }

    @Test
    void 비로그인_사용자가_게시글_좋아요_취소_요청하면_401_반환() throws Exception {

        // given
        LikeRequestDto likeRequestDto = new LikeRequestDto(1L);

        // when & then
        mockMvc.perform(delete("/api/v1/likes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(likeRequestDto))
        )
                .andExpect(status().isUnauthorized());
    }
}