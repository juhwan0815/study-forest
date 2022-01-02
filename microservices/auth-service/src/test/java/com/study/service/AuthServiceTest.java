package com.study.service;

import com.study.AuthFixture;
import com.study.client.KakaoClient;
import com.study.client.UserServiceClient;
import com.study.domain.Auth;
import com.study.dto.KakaoProfile;
import com.study.dto.TokenResponse;
import com.study.dto.UserResponse;
import com.study.exception.TokenNotMatchException;
import com.study.repository.AuthRepository;
import com.study.utils.jwt.JwtUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.study.AuthFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private KakaoClient kakaoClient;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private AuthRepository authRepository;

    @Test
    @DisplayName("카카오 로그인을 한다.")
    void login() {
        // given
        KakaoProfile.Properties properties =
                new KakaoProfile.Properties("황주환", "이미지", "이미지");

        KakaoProfile.KakaoAccount kakaoAccount =
                new KakaoProfile.KakaoAccount("10~19", "male");

        KakaoProfile kakaoProfile = new KakaoProfile(1L, properties, kakaoAccount);

        given(kakaoClient.getKakaoProfile(any()))
                .willReturn(kakaoProfile);

        given(userServiceClient.loginByKakaoId(any(), any()))
                .willReturn(new UserResponse(1L, "USER", "황주환"));

        given(jwtUtils.createToken(any(), any(), any(), any()))
                .willReturn("accessToken")
                .willReturn("refreshToken");

        given(authRepository.save(any()))
                .willReturn(TEST_AUTH);

        // when
        TokenResponse result = authService.login("kakaoToken", "fcmToken");

        // then
        assertThat(result.getAccessToken()).isEqualTo("accessToken");
        assertThat(result.getRefreshToken()).isEqualTo("refreshToken");
        then(kakaoClient).should(times(1)).getKakaoProfile(any());
        then(userServiceClient).should(times(1)).loginByKakaoId(any(), any());
        then(jwtUtils).should(times(2)).createToken(any(), any(), any(), any());
        then(authRepository).should(times(1)).save(any());
    }

    @Test
    @DisplayName("refresh 토큰으로 accessToken 과 refreshToken 을 갱신한다.")
    void refreshAccessTokenAndRefreshToken() {
        // given
        Auth auth = Auth.createAuth(1L, "refreshToken", 5L);

        given(authRepository.findById(any()))
                .willReturn(Optional.of(auth));

        given(jwtUtils.createToken(any(), any()))
                .willReturn("accessToken")
                .willReturn("refreshToken");

        // when
        TokenResponse result = authService.refresh(1L, "refreshToken");

        // then
        assertThat(result.getAccessToken()).isEqualTo("accessToken");
        assertThat(result.getRefreshToken()).isEqualTo("refreshToken");
        then(authRepository).should(times(1)).findById(any());
        then(jwtUtils).should(times(2)).createToken(any(), any());
        then(authRepository).should(times(1)).save(any());
    }

    @Test
    @DisplayName("refresh 토큰으로 accessToken 만 갱신한다.")
    void refreshAccessToken() {
        // given
        given(authRepository.findById(any()))
                .willReturn(Optional.of(TEST_AUTH));

        given(jwtUtils.createToken(any(), any()))
                .willReturn("accessToken");

        // when
        TokenResponse result = authService.refresh(1L, "refreshToken");

        // then
        assertThat(result.getAccessToken()).isEqualTo("accessToken");
        assertThat(result.getRefreshToken()).isNull();
        then(authRepository).should(times(1)).findById(any());
        then(jwtUtils).should(times(1)).createToken(any(), any());
    }

    @Test
    @DisplayName("예외 테스트 : refreshToken 이 일치하지 않을 경우 예외가 발생한다.")
    void ifNotMatchRefreshToken(){
        Auth auth = Auth.createAuth(1L, "refreshToken1", 30L);

        // given
        given(authRepository.findById(any()))
                .willReturn(Optional.of(auth));

        // when
        assertThrows(TokenNotMatchException.class, () -> authService.refresh(1L, "refreshToken"));
    }
}