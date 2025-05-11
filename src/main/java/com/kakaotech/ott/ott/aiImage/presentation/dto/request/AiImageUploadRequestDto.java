package com.kakaotech.ott.ott.aiImage.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class AiImageUploadRequestDto {

    @NotNull(message = "이미지는 필수 항목입니다.")
    @JsonProperty("before_image_path")
    private MultipartFile beforeImagePath;

}
