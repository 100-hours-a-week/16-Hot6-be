package com.kakaotech.ott.ott.post.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PopularSetupDto {

    private Long postId;

    private String title;

    private String thumbnailUrl;

    private boolean scrapped;
}
