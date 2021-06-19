package com.study.studyservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@NoArgsConstructor
@Getter
public class Image {

    @JsonIgnore
    private String imageStoreName; // 이미지 저장 명

    private String studyImage; // 이미지 URL

    private String thumbnailImage; // 썸네일 이미지 URL

    public static Image createImage(String studyImage,String thumbnailImage,String imageStoreName){
        Image image = new Image();
        image.studyImage = studyImage;
        image.thumbnailImage = thumbnailImage;
        image.imageStoreName = imageStoreName;
        return image;
    }
}
