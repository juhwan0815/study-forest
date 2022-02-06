package com.study.service;

import com.study.client.KakaoClient;
import com.study.client.KakaoProfile;
import com.study.client.UserServiceClient;
import com.study.domain.Auth;
import com.study.dto.TokenResponse;
import com.study.dto.UserResponse;
import com.study.exception.NotFoundException;
import com.study.exception.NotMatchException;
import com.study.repository.AuthRepository;
import com.study.utils.JwtUtils;
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
                .willReturn(TEST_AUTH_ACCESS_TOKEN)
                .willReturn(TEST_AUTH_REFRESH_TOKEN);

        given(authRepository.save(any()))
                .willReturn(TEST_AUTH);

        // when
        TokenResponse result = authService.login(TEST_KAKAO_TOKEN, TEST_FCM_TOKEN);

        // thenR
        assertThat(result.getAccessToken()).isEqualTo(TEST_AUTH_ACCESS_TOKEN);
        assertThat(result.getRefreshToken()).isEqualTo(TEST_AUTH_REFRESH_TOKEN);
        then(kakaoClient).should(times(1)).getKakaoProfile(any());
        then(userServiceClient).should(times(1)).loginByKakaoId(any(), any());
        then(jwtUtils).should(times(2)).createToken(any(), any(), any(), any());
        then(authRepository).should(times(1)).save(any());
    }

    @Test
    @DisplayName("accessToken 과 refreshToken 을 갱신한다.")
    void refreshAccessTokenAndRefreshToken() {
        // given
        Auth auth = Auth.createAuth(TEST_AUTH.getUserId(), TEST_AUTH_REFRESH_TOKEN, 5L);

        given(authRepository.findById(any()))
                .willReturn(Optional.of(auth));

        given(jwtUtils.createToken(any(), any()))
                .willReturn(TEST_AUTH_ACCESS_TOKEN)
                .willReturn(TEST_AUTH_REFRESH_TOKEN);

        // when
        TokenResponse result = authService.refresh(TEST_AUTH.getUserId(), TEST_AUTH_REFRESH_TOKEN);

        // then
        assertThat(result.getAccessToken()).isEqualTo(TEST_AUTH_ACCESS_TOKEN);
        assertThat(result.getRefreshToken()).isEqualTo(TEST_AUTH_REFRESH_TOKEN);
        then(authRepository).should(times(1)).findById(any());
        then(jwtUtils).should(times(2)).createToken(any(), any());
        then(authRepository).should(times(1)).save(any());
    }

    @Test
    @DisplayName("accessToken 을 갱신한다.")
    void refreshAccessToken() {
        // given
        given(authRepository.findById(any()))
                .willReturn(Optional.of(TEST_AUTH));

        given(jwtUtils.createToken(any(), any()))
                .willReturn(TEST_AUTH_ACCESS_TOKEN);

        // when
        TokenResponse result = authService.refresh(TEST_AUTH.getUserId(), TEST_AUTH_REFRESH_TOKEN);

        // then
        assertThat(result.getAccessToken()).isEqualTo(TEST_AUTH_ACCESS_TOKEN);
        assertThat(result.getRefreshToken()).isNull();
        then(authRepository).should(times(1)).findById(any());
        then(jwtUtils).should(times(1)).createToken(any(), any());
    }

    @Test
    @DisplayName("토큰을 갱신할 때 refreshToken 이 일치하지 않을 경우 예외가 발생한다.")
    void refreshNotMatch() {
        // given
        Auth auth = Auth.createAuth(TEST_AUTH.getUserId(), "refreshToken1", TEST_AUTH.getExpiration());

        given(authRepository.findById(any()))
                .willReturn(Optional.of(auth));

        // when
        assertThrows(NotMatchException.class, () -> authService.refresh(TEST_AUTH.getUserId(), TEST_AUTH_REFRESH_TOKEN));

        // then
        then(authRepository).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("토큰을 갱신할 때 인증이 존재하지 않는 경우 예외가 발생한다.")
    void refreshNotFound() {
        // given
        given(authRepository.findById(any()))
                .willReturn(Optional.empty());

        // when
        assertThrows(NotFoundException.class, () -> authService.refresh(TEST_AUTH.getUserId(), TEST_AUTH_REFRESH_TOKEN));

        // then
        then(authRepository).should(times(1)).findById(any());
    }
}