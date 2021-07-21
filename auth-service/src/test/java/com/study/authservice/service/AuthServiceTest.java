package com.study.authservice.service;

import com.study.authservice.AuthFixture;
import com.study.authservice.client.KakaoServiceClient;
import com.study.authservice.client.UserServiceClient;
import com.study.authservice.domain.Auth;
import com.study.authservice.exception.AuthException;
import com.study.authservice.model.common.CreateTokenResult;
import com.study.authservice.model.common.KakaoProfile;
import com.study.authservice.model.user.UserResponse;
import com.study.authservice.repository.AuthRepository;
import com.study.authservice.service.Impl.AuthServiceImpl;
import com.study.authservice.util.JwtTokenProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

import static com.study.authservice.AuthFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private KakaoServiceClient kakaoServiceClient;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthRepository authRepository;

    @Test
    @DisplayName("카카오 토큰으로 로그인 - 이미 가입된 회원일 경우")
    void loginExistUser(){

        // given
        KakaoProfile.Properties properties =
                new KakaoProfile.Properties("황주환","이미지","이미지");

        KakaoProfile.KakaoAccount kakaoAccount =
                new KakaoProfile.KakaoAccount("10~19","male");

        KakaoProfile kakaoProfile =
                new KakaoProfile(1L,properties,kakaoAccount);

        given(kakaoServiceClient.getKakaoProfile(any()))
                .willReturn(kakaoProfile);

        given(userServiceClient.login(any()))
                .willReturn(TEST_USER_RESPONSE);

        given(jwtTokenProvider.createToken(any(),any(),any()))
                .willReturn(TEST_CREATE_TOKEN_RESULT);

        given(authRepository.findByUserId(any()))
                .willReturn(Optional.of(TEST_AUTH));

        // when
        CreateTokenResult tokenResult = authService.login("kakaoToken","fcmToken");

        // then
        assertThat(tokenResult.getAccessToken()).isEqualTo(TEST_ACCESS_TOKEN);
        assertThat(tokenResult.getRefreshToken()).isEqualTo(TEST_REFRESH_TOKEN);
        then(kakaoServiceClient).should(times(1)).getKakaoProfile(any());
        then(userServiceClient).should(times(1)).login(any());
        then(jwtTokenProvider).should(times(1)).createToken(any(),any(),any());
        then(authRepository).should(times(1)).findByUserId(any());
    }

    @Test
    @DisplayName("카카오 토큰으로 로그인 - 이미 가입된 회원이 아닐 경우")
    void loginNotExistUser(){

        // given
        KakaoProfile.Properties properties =
                new KakaoProfile.Properties("황주환","이미지","이미지");

        KakaoProfile.KakaoAccount kakaoAccount =
                new KakaoProfile.KakaoAccount("10~19","male");

        KakaoProfile kakaoProfile =
                new KakaoProfile(1L,properties,kakaoAccount);

        given(kakaoServiceClient.getKakaoProfile(any()))
                .willReturn(kakaoProfile);

        given(userServiceClient.login(any()))
                .willReturn(TEST_USER_RESPONSE);

        given(jwtTokenProvider.createToken(any(),any(),any()))
                .willReturn(TEST_CREATE_TOKEN_RESULT);

        given(authRepository.findByUserId(any()))
                .willReturn(Optional.empty());

        given(authRepository.save(any()))
                .willReturn(TEST_AUTH);

        // when
        CreateTokenResult result = authService.login("kakaoToken","fcmToken");

        // then
        assertThat(result.getAccessToken()).isEqualTo(TEST_ACCESS_TOKEN);
        assertThat(result.getRefreshToken()).isEqualTo(TEST_REFRESH_TOKEN);
        then(kakaoServiceClient).should(times(1)).getKakaoProfile(any());
        then(userServiceClient).should(times(1)).login(any());
        then(jwtTokenProvider).should(times(1)).createToken(any(),any(),any());
        then(authRepository).should(times(1)).findByUserId(any());
        then(authRepository).should(times(1)).save(any());
    }

    @Test
    @DisplayName("Refresh 토큰으로 Access 토큰을 재발급 받는다.")
    void refreshTokenNotError(){
        // given
        given(authRepository.findByUserId(any()))
                .willReturn(Optional.of(TEST_AUTH));

        given(jwtTokenProvider.refresh(any(),any()))
                .willReturn(TEST_ACCESS_TOKEN);

        // when
        String result = authService.refresh(TEST_REFRESH_TOKEN, 1L);

        // then
        assertThat(result).isEqualTo(TEST_ACCESS_TOKEN);

        then(authRepository).should(times(1)).findByUserId(any());
        then(jwtTokenProvider).should(times(1)).refresh(any(),any());
    }

    @Test
    @DisplayName("로그아웃")
    void logout() {
        // given
        given(authRepository.findByUserId(any()))
                .willReturn(Optional.of(TEST_AUTH));

        willDoNothing()
                .given(authRepository)
                .delete(any());
        // when
        authService.delete(1L);

        // then
        then(authRepository).should(times(1)).findByUserId(any());
        then(authRepository).should(times(1)).delete(any());
    }


}
