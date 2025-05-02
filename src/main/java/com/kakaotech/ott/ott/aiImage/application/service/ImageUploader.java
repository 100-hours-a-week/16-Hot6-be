package com.kakaotech.ott.ott.aiImage.application.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageUploader {
    String upload(MultipartFile image) throws IOException;
}
