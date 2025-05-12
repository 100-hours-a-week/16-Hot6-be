package com.kakaotech.ott.ott.aiImage.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FastApiRequestDto {

    @JsonProperty("initial_image_url")
    private String initialImageUrl;

//    @JsonProperty("user_id")
//    private Long user_id;

    public void fastApiUrl(String domain) {
        this.initialImageUrl = domain + this.initialImageUrl;
    }
}
