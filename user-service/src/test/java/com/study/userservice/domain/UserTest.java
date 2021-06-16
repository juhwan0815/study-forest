package com.study.userservice.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    @DisplayName("회원의 이미지를 수정한다.")
    void changeImage(){
        User user = User.createUser(1L, "황주환", "10~19", "male", UserRole.USER);
        Image image = Image.createImage("썸네일 이미지", "이미지", "이미지 저장 이름");

        user.changeImage(image);

        assertThat(user.getImage()).isNotNull();
        assertThat(user.getImage().getProfileImage()).isEqualTo("이미지");
        assertThat(user.getImage().getThumbnailImage()).isEqualTo("썸네일 이미지");
        assertThat(user.getImage().getImageStoreName()).isEqualTo("이미지 저장 이름");
    }

    @Test
    @DisplayName("회원의 닉네임을 수정한다.")
    void changeNickName(){
        // given
        User user = User.createUser(1L,"황주환","10~19","male", UserRole.USER);

        // when
        user.changeNickName("황철원");

        // then
        assertThat(user.getNickName()).isEqualTo("황철원");
    }

}