package com.kakaotech.ott.ott.post.presentation.dto.request;

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
public class FreePostUpdateRequestDto {

    private String title;
    private String content;

    @Size(max = 3, message = "최대 3장까지 업로드 가능합니다.")
    private List<MultipartFile> images;
}
