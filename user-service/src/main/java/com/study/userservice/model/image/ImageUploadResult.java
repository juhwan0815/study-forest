package com.study.userservice.model.image;

import lombok.Data;

@Data
public class ImageUploadResult {

    private String profileImage;

    private String thumbnailImage;

    private String imageStoreName;

    public static ImageUploadResult from(String profileImage,String thumbnailImage,
                                         String imageStoreName){
        ImageUploadResult imageUploadResult = new ImageUploadResult();
        imageUploadResult.profileImage = profileImage;
        imageUploadResult.thumbnailImage = thumbnailImage;
        imageUploadResult.imageStoreName = imageStoreName;
        return imageUploadResult;
    }



}
