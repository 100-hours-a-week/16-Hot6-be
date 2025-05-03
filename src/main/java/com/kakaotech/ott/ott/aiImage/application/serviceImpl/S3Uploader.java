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
        String ulidName = ulid.nextULID();
        String imageName = "assets/images/" + ulidName + extension;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(image.getSize());
        metadata.setContentType(image.getContentType());

        amazonS3.putObject(
                new PutObjectRequest(bucket, imageName, image.getInputStream(), metadata)
        );

        return "https://img.onthe-top.com/" + ulidName + extension;
    }

    private String getExtension(String originalName) {
        if (originalName == null || !originalName.contains(".")) {
            return "";
        }
        return originalName.substring(originalName.lastIndexOf("."));
    }

    @Override
    public void delete(String imageName) {
        // imageName은 예: 01H...X2.png 와 같은 키
        if (amazonS3.doesObjectExist(bucket, imageName)) {
            amazonS3.deleteObject(bucket, imageName);
            System.out.println("✅ S3에서 이미지 삭제 완료: " + imageName);
        } else {
            System.out.println("⚠️ 삭제 실패 - 해당 파일이 존재하지 않습니다: " + imageName);
        }
    }
}
