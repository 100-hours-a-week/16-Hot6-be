package com.kakaotech.ott.ott.util.validator;

import com.kakaotech.ott.ott.productOrder.presentation.dto.request.ProductOrderRequestDto;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class OrderFingerprintUtil {
    public static String generateFingerprint(List<ProductOrderRequestDto.ServiceProductDto> items) {
        // 1. 정렬 (variantId 기준 정렬)
        List<String> parts = items.stream()
                .sorted(Comparator.comparingLong(ProductOrderRequestDto.ServiceProductDto::getVariantId))
                .map(item -> item.getVariantId() + ":" + item.getQuantity())
                .collect(Collectors.toList());

        // 2. "1:2|2:1|5:3" 형식의 문자열 생성
        String rawString = String.join("|", parts);

        // 3. SHA-256 해싱
        return sha256(rawString);
    }

    private static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
