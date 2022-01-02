package com.study.domain;

import com.study.exception.KeywordDuplicateException;
import com.study.exception.KeywordNotFoundException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("회원을 생성한다.")
    void createUser() {
        // given
        Long kakaoId = 1L;
        String nickName = "황주환";
        String ageRange = "10~19";
        String gender = "male";
        UserRole role = UserRole.USER;

        // when
        User result = User.createUser(kakaoId, nickName, ageRange, gender, role);

        // then
        assertThat(result.getKakaoId()).isEqualTo(kakaoId);
        assertThat(result.getNickName()).isEqualTo(nickName);
        assertThat(result.getAgeRange()).isEqualTo(ageRange);
        assertThat(result.getGender()).isEqualTo(gender);
        assertThat(result.getRole()).isEqualTo(role);
        assertThat(result.getDistance()).isEqualTo(3);
    }

    @Test
    @DisplayName("이미지를 변경한다.")
    void changeImage() {
        // given
        User user = User.createUser(1L, "황주환", "10~19", "male", UserRole.USER);
        Image image = Image.createImage("이미지 URL", "이미지 저장 이름");

        // when
        user.changeImage(image);

        // then
        assertThat(user.getImage()).isEqualTo(image);
    }

    @Test
    @DisplayName("FCM 토큰을 변경한다.")
    void changeFcmToken() {
        // given
        User user = User.createUser(1L, "황주환", "10~19", "male", UserRole.USER);

        // when
        user.changeFcmToken("fcmToken");

        // then
        assertThat(user.getFcmToken()).isEqualTo("fcmToken");
    }

    @Test
    @DisplayName("프로필을 변경한다.")
    void changeProfile() {
        // given
        User user = User.createUser(1L, "황주환", "10~19", "male", UserRole.USER);

        // when
        user.changeProfile("황철원");

        // then
        assertThat(user.getNickName()).isEqualTo("황철원");
    }

    @Test
    @DisplayName("지역을 변경한다.")
    void changeArea() {
        // given
        User user = User.createUser(1L, "황주환", "10~19", "male", UserRole.USER);

        // when
        user.changeArea(1L);

        // then
        assertThat(user.getAreaId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("검색거리를 변경한다.")
    void changeDistance() {
        // given
        User user = User.createUser(1L, "황주환", "10~19", "male", UserRole.USER);

        // when
        user.changeDistance(6);

        // then
        assertThat(user.getDistance()).isEqualTo(6);
    }

    @Test
    @DisplayName("관심 키워드를 추가한다.")
    void addKeyword(){
        // given
        User user = User.createUser(1L, "황주환", "10~19", "male", UserRole.USER);

        // when
        user.addKeyword("개발");

        // then
        assertThat(user.getKeywords().size()).isEqualTo(1);
        assertThat(user.getKeywords().get(0).getContent()).isEqualTo("개발");
    }

    @Test
    @DisplayName("예외 테스트 : 중복된 관심 키워드를 추가하면 예외가 발생한다.")
    void addDuplicateKeyword(){
        // given
        User user = User.createUser(1L, "황주환", "10~19", "male", UserRole.USER);
        user.addKeyword("개발");

        // when
        assertThrows(KeywordDuplicateException.class, ()-> user.addKeyword("개발"));
    }

    @Test
    @DisplayName("관심 키워드를 삭제한다.")
    void deleteKeyword(){
        // given
        User user = User.createUser(1L, "황주환", "10~19", "male", UserRole.USER);
        user.getKeywords().add(new Keyword(1L, "개발",user));

        // when
        user.deleteKeyword(1L);

        // then
        assertThat(user.getKeywords().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("예외테스트 : 존재하지 않는 관심 키워드를 제거하면 예외가 발생한다.")
    void deleteNotExistKeyword(){
        // given
        User user = User.createUser(1L, "황주환", "10~19", "male", UserRole.USER);

        // when
        assertThrows(KeywordNotFoundException.class, ()-> user.deleteKeyword(1L));
    }

}