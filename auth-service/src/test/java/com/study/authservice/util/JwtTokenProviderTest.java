package com.study.authservice.util;

import com.study.authservice.exception.AuthException;
import com.study.authservice.model.common.CreateTokenResult;
import com.study.authservice.model.user.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.study.authservice.AuthFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("토큰 생성 테스트")
    void createToken(){
        // when
        CreateTokenResult result = jwtTokenProvider.createToken(TEST_AUTH.getId(), TEST_AUTH.getRefreshToken());

        // then
        assertThat(result.getAccessToken()).isNotNull();
        assertThat(result.getRefreshToken()).isNotNull();
    }

    @Test
    @DisplayName("토큰에서 UserId 추출 테스트")
    void extractUserId(){
        // when
        Long result = jwtTokenProvider.getUserId("Bearer " + TEST_REFRESH_TOKEN);

        // then
        assertThat(result).isEqualTo(TEST_USER_RESPONSE.getId());
    }

    @Test
    @DisplayName("예외테스트 : 잘못된 Refresh 토큰으로 Access 토큰 재발급")
    void ifNotMatchRefreshToken(){
        assertThrows(AuthException.class,()->jwtTokenProvider.refresh("잘못된 토큰", TEST_AUTH));
    }

    @Test
    @DisplayName("Refresh 토큰이 일치하는 경우 새로운 Access 토큰 발급")
    void ifMatchRefreshToken(){

        // when
        String result = jwtTokenProvider.refresh(TEST_REFRESH_TOKEN, TEST_AUTH);

        // then
        assertThat(result).isNotNull();
    }

}