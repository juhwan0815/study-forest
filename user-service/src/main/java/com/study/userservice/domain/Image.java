package com.study.userservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {

    private String thumbnailImage; // 썸네일 이미지

    private String profileImage; // 프로필 이미지

    @JsonIgnore
    private String imageStoreName; // 프로필 이미지 저장이름

    public static Image createImage(String thumbnailImage, String profileImage, String imageStoreName){
        Image image = new Image();
        image.thumbnailImage = thumbnailImage;
        image.profileImage = profileImage;
        image.imageStoreName = imageStoreName;
        return image;
    }
}
