package com.study.studyservice.model.image;

import lombok.Data;

@Data
public class ImageUploadResult {

    private String studyImage;

    private String studyThumbnailImage;

    private String imageStoreName;

    public static ImageUploadResult from(String studyImage,
                                         String studyThumbnailImage,
                                         String imageStoreName){
        ImageUploadResult imageUploadResult = new ImageUploadResult();
        imageUploadResult.studyImage = studyImage;
        imageUploadResult.studyThumbnailImage = studyThumbnailImage;
        imageUploadResult.imageStoreName = imageStoreName;
        return imageUploadResult;
    }
}
