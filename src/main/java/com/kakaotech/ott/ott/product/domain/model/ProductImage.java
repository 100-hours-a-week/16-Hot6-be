package com.kakaotech.ott.ott.product.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductImage {

    private Long id;
    private Long productId;
    private int sequence;
    private String imageUuid;

    // 이미지 생성 팩토리 메서드
    public static ProductImage createImage(
            Long productId,
            int sequence,
            String imageUuid) {

        // 비즈니스 검증
        validateSequence(sequence);
        validateImageUuid(imageUuid);

        return ProductImage.builder()
                .productId(productId)
                .sequence(sequence)
                .imageUuid(imageUuid)
                .build();
    }

    // 시퀀스 검증
    private static void validateSequence(int sequence) {
        if (sequence < 0) {
            throw new IllegalArgumentException("이미지 순서는 0 이상이어야 합니다.");
        }
    }

    // 이미지 UUID 검증
    private static void validateImageUuid(String imageUuid) {
        if (imageUuid == null || imageUuid.trim().isEmpty()) {
            throw new IllegalArgumentException("이미지 UUID는 필수입니다.");
        }
    }

    // 시퀀스 수정
    public void updateSequence(int sequence) {
        validateSequence(sequence);
        this.sequence = sequence;
    }
}