package com.kakaotech.ott.ott.post.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kakaotech.ott.ott.post.domain.model.PostType;
import com.kakaotech.ott.ott.util.KstDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostGetResponseDto {

    private Long postId;

    private String title;

    private String content;

    private PostType type;

    private PostAuthorResponseDto author;

    private long likeCount;

    private int commentCount;

    private long viewCount;

    private boolean scrapped;

    private boolean liked;

    private boolean isOwner;

    private List<?> imageUrls;

    @JsonProperty("createdAt")
    private KstDateTime myCustomTime;

}
