package com.study.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ImageTest {

    @Test
    @DisplayName("이미지를 생성한다.")
    void createImage() {
        // given
        String imageUrl = "이미지 URL";
        String imageStoreName = "이미지 저장 이름";

        // when
        Image result = Image.createImage(imageUrl, imageStoreName);

        // then
        assertThat(result.getImageUrl()).isEqualTo(imageUrl);
        assertThat(result.getImageStoreName()).isEqualTo(imageStoreName);
    }
}