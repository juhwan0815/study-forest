package com.study.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.study.domain.Image;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ImageUtil {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public Image uploadImage(MultipartFile file, Image userImage) {

        Image returnImage = null;

        if (file == null) {
            deleteImageFromS3(userImage);
        } else {
            if (!file.isEmpty()) {
                deleteImageFromS3(userImage);
                validateImageType(file);
                returnImage = uploadImageToS3(file);
            }
        }
        return returnImage;
    }

    private void deleteImageFromS3(Image image) {
        if (image != null) {
            if (image.getImageStoreName() != null) {
                amazonS3Client.deleteObject(bucket, image.getImageStoreName());
            }
        }
    }

    private void validateImageType(MultipartFile image) {
        if (!image.getContentType().startsWith("image")) {
            throw new RuntimeException("이미지의 파일타입이 잘못되었습니다.");
        }
    }

    private Image uploadImageToS3(MultipartFile image) {
        String ext = extractExt(image.getContentType());
        String imageStoreName = UUID.randomUUID().toString() + "." + ext;

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(image.getSize());
        objectMetadata.setContentType(image.getContentType());

        Image uploadResult = null;
        try {
            amazonS3Client.putObject(new PutObjectRequest(bucket, imageStoreName, image.getInputStream(), objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead));
            String imageUrl = amazonS3Client.getUrl(bucket, imageStoreName).toString();
            uploadResult = Image.createImage(imageUrl, imageStoreName);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return uploadResult;
    }

    private String extractExt(String contentType) {
        int pos = contentType.lastIndexOf("/");
        return contentType.substring(pos + 1);
    }
}
