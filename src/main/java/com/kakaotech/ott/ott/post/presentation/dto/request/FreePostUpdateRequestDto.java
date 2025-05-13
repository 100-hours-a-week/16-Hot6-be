package com.kakaotech.ott.ott.post.presentation.dto.request;

import com.kakaotech.ott.ott.util.validator.ValidImageCount;
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
@ValidImageCount
public class FreePostUpdateRequestDto {

    private String title;
    private String content;

    private List<String> existingImageIds; // 유지할 기존 이미지 ID 목록

    @Size(max = 5, message = "최대 5장까지 업로드 가능합니다.")
    private List<MultipartFile> images;
}
