package com.study.authservice.util;

import com.study.authservice.exception.AuthException;
import com.study.authservice.model.CreateTokenResult;
import com.study.authservice.model.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("토큰 생성 테스트")
    void createToken(){
        // given
        UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setKakaoId(1L);
        userResponse.setNickName("황주환");
        userResponse.setProfileImage("이미지");
        userResponse.setThumbnailImage("이미지");
        userResponse.setStatus("ACTIVE");
        userResponse.setRole("USER");

        // when
        CreateTokenResult result = jwtTokenProvider.createToken(userResponse);

        // then
        assertThat(result.getAccessToken()).isNotNull();
        assertThat(result.getRefreshToken()).isNotNull();
    }

    @Test
    @DisplayName("토큰에서 UserId 추출 테스트")
    void extractUserId(){
        // given
        UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setKakaoId(1L);
        userResponse.setNickName("황주환");
        userResponse.setProfileImage("이미지");
        userResponse.setThumbnailImage("이미지");
        userResponse.setStatus("ACTIVE");
        userResponse.setRole("ROLE");

        CreateTokenResult token = jwtTokenProvider.createToken(userResponse);
        System.out.println(token.getRefreshToken());

        // when
        Long result = jwtTokenProvider.getUserId("Bearer " + token.getRefreshToken());


        // then
        assertThat(result).isEqualTo(userResponse.getId());
    }

    @Test
    @DisplayName("Refresh 토큰이 일치하지 않는 경우 에러 발생")
    void ifNotMatchRefreshToken(){
        // given
        UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setKakaoId(1L);
        userResponse.setNickName("황주환");
        userResponse.setProfileImage("이미지");
        userResponse.setThumbnailImage("이미지");
        userResponse.setStatus("ACTIVE");
        userResponse.setRole("USER");

        CreateTokenResult token = jwtTokenProvider.createToken(userResponse);

        userResponse.setRefreshToken(token.getRefreshToken());
        userResponse.setId(2L);

        String refreshToken = jwtTokenProvider.createRefreshToken(userResponse);

        // when
        assertThrows(AuthException.class,()->jwtTokenProvider.refresh(refreshToken, userResponse));
    }

    @Test
    @DisplayName("Refresh 토큰이 일치하는 경우 새로운 토큰 발급")
    void ifMatchRefreshToken(){
        // given
        UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setKakaoId(1L);
        userResponse.setNickName("황주환");
        userResponse.setProfileImage("이미지");
        userResponse.setThumbnailImage("이미지");
        userResponse.setStatus("ACTIVE");
        userResponse.setRole("USER");

        CreateTokenResult token = jwtTokenProvider.createToken(userResponse);
        userResponse.setRefreshToken(token.getRefreshToken());

        // when
        CreateTokenResult result = jwtTokenProvider.refresh(token.getRefreshToken(), userResponse);

        // then
        assertThat(result.getAccessToken()).isNotNull();
        assertThat(result.getRefreshToken()).isNotNull();
    }

}