package com.kakaotech.ott.ott.aiImage.application.serviceImpl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.kakaotech.ott.ott.aiImage.application.service.ImageUploader;
import de.huxhorn.sulky.ulid.ULID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class S3Uploader implements ImageUploader {

    private final AmazonS3 amazonS3;
    private static final ULID ulid = new ULID();

    @Value("${cloud.aws.s3.bucketName}")
    private String bucket;

    @Override
    public String upload(MultipartFile image) throws IOException {
        String extension = getExtension(image.getOriginalFilename());
        String ulidName = ulid.nextULID() + extension;
        String imageUrl = "assets/images/" + ulidName;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(image.getSize());
        metadata.setContentType(image.getContentType());

        amazonS3.putObject(
                new PutObjectRequest(bucket, imageUrl, image.getInputStream(), metadata)
        );

        return ulidName;
    }

    private String getExtension(String originalName) {
        if (originalName == null || !originalName.contains(".")) {
            return "";
        }
        return originalName.substring(originalName.lastIndexOf("."));
    }

    @Override
    public void delete(String imageUrl) {
        String key = "assets/images/" + imageUrl;
        amazonS3.deleteObject(bucket, key);
    }
}
