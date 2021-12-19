package com.study.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {

    private String imageUrl; // 이미지 URL

    private String imageStoreName; // 이미지 저장 이름

    public static Image createImage(String imageUrl, String imageStoreName){
        Image image = new Image();
        image.imageUrl = imageUrl;
        image.imageStoreName = imageStoreName;
        return image;
    }
}
