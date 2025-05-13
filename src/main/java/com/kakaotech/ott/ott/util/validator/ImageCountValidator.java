package com.kakaotech.ott.ott.util.validator;

import com.kakaotech.ott.ott.post.presentation.dto.request.FreePostUpdateRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ImageCountValidator implements ConstraintValidator<ValidImageCount, FreePostUpdateRequestDto> {

    @Override
    public boolean isValid(FreePostUpdateRequestDto freePostUpdateRequestDto, ConstraintValidatorContext constraintValidatorContext) {

        // exstingImageIds가 null일 경우 빈 리스트로 처리
        int existingCount = (freePostUpdateRequestDto.getExistingImageIds() != null) ? freePostUpdateRequestDto.getExistingImageIds().size() : 0;
        int maxCount = 5 - existingCount;

        // image가 null이면 0으로 처리
        int uploadedCount = (freePostUpdateRequestDto.getImages() != null) ? freePostUpdateRequestDto.getImages().size() : 0;

        // 업로드할 이미지가 최대 개수보다 크면 유효성 검사 실패
        return uploadedCount <= maxCount;
    }
}
