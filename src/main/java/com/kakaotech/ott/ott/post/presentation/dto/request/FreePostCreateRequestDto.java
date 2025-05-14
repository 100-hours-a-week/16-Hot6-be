package com.kakaotech.ott.ott.post.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FreePostCreateRequestDto {

    @NotBlank(message = "게시글 제목은 필수입니다.")
    private String title;

    @NotBlank(message = "게시글 내용은 필수입니다.")
    private String content;

    @Size(max = 5, message = "이미지는 최대 5개까지 업로드할 수 있습니다.")
    private List<MultipartFile> images;
}
