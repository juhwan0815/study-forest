package com.study.userservice.domain;

import com.study.userservice.exception.UserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    @DisplayName("회원의 지역을 변경한다.")
    void changeLocation(){
        // given
        User user = User.createUser(1L,"황주환","10~19","male", UserRole.USER);

        // when
        user.changeLocation(1L);

        // then
        assertThat(user.getLocationId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("예외 테스트 : 이미 관심주제에 존재하는 태그를 추가할 경우 예외가 발생한다.")
    void checkExistTag(){
        // given
        User user = User.createUser(1L,"황주환","10~19","male", UserRole.USER);
        user.addInterestTag(1L);

        // when
        assertThrows(UserException.class,()-> user.checkExistTag(1L));
    }

    @Test
    @DisplayName("관심 주제에 태그를 추가한다.")
    void addInterestTag(){
        // given
        User user = User.createUser(1L,"황주환","10~19","male", UserRole.USER);

        // when
        user.addInterestTag(1L);

        // then
        assertThat(user.getInterestTags().size()).isEqualTo(1);
        assertThat(user.getInterestTags().get(0).getTagId()).isEqualTo(1L);
    }
}