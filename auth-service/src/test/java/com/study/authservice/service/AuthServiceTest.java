package com.study.authservice.service;

import com.study.authservice.client.KakaoServiceClient;
import com.study.authservice.client.UserServiceClient;
import com.study.authservice.kafka.sender.KafkaLogoutMessageSender;
import com.study.authservice.kafka.sender.KafkaRefreshTokenCreateMessageSender;
import com.study.authservice.model.CreateTokenResult;
import com.study.authservice.model.KakaoProfile;
import com.study.authservice.model.UserResponse;
import com.study.authservice.service.Impl.AuthServiceImpl;
import com.study.authservice.util.JwtTokenProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthServiceImpl authServiceImpl;

    @Mock
    private KakaoServiceClient kakaoServiceClient;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private KafkaRefreshTokenCreateMessageSender kafkaRefreshTokenCreateMessageSender;

    @Mock
    private KafkaLogoutMessageSender kafkaLogoutMessageSender;


    @Test
    @DisplayName("카카오 계정 로그인")
    void login(){

        // given
        String kakaoToken = "kakaoToken";

        KakaoProfile kakaoProfile = new KakaoProfile();

        KakaoProfile.Properties properties = new KakaoProfile.Properties();
        properties.setNickname("황주환");
        properties.setProfile_image("어딘가의 이미지");
        properties.setThumbnail_image("어딘가의 이미지");

        KakaoProfile.KakaoAccount kakaoAccount = new KakaoProfile.KakaoAccount();
        kakaoAccount.setAge_range("10~19");
        kakaoAccount.setGender("male");

        kakaoProfile.setId(1L);
        kakaoProfile.setProperties(properties);
        kakaoProfile.setKakao_account(kakaoAccount);

        UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setKakaoId(kakaoProfile.getId());
        userResponse.setNickName(kakaoProfile.getProperties().getNickname());
        userResponse.setProfileImage(kakaoProfile.getProperties().getProfile_image());
        userResponse.setThumbnailImage(kakaoProfile.getProperties().getThumbnail_image());
        userResponse.setGender("male");
        userResponse.setAgeRange("10~19");
        userResponse.setStatus("ACTIVE");
        userResponse.setRole("USER");

        String accessToken = Jwts.builder()
                .setSubject(String.valueOf(userResponse.getId()))
                .claim("ROLE", userResponse.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + 86400000L))
                .signWith(SignatureAlgorithm.HS256, "study")
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(String.valueOf(userResponse.getId()))
                .claim("ROLE", userResponse.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + 604800000L))
                .signWith(SignatureAlgorithm.HS256, "study")
                .compact();

        CreateTokenResult createTokenResult = new CreateTokenResult();
        createTokenResult.setAccessToken(accessToken);
        createTokenResult.setRefreshToken(refreshToken);

        given(kakaoServiceClient.getKakaoProfile(any()))
                .willReturn(kakaoProfile);

        given(userServiceClient.login(any()))
                .willReturn(userResponse);

        given(jwtTokenProvider.createToken(any()))
                .willReturn(createTokenResult);

        willDoNothing()
                .given(kafkaRefreshTokenCreateMessageSender)
                .send(any());

        // when
        CreateTokenResult tokenResult = authServiceImpl.login(kakaoToken);

        // then
        assertThat(tokenResult.getAccessToken()).isEqualTo(accessToken);
        assertThat(tokenResult.getRefreshToken()).isEqualTo(refreshToken);
        then(kakaoServiceClient).should(times(1)).getKakaoProfile(any());
        then(userServiceClient).should(times(1)).login(any());
        then(jwtTokenProvider).should(times(1)).createToken(any());
        then(kafkaRefreshTokenCreateMessageSender).should(times(1)).send(any());
    }

    @Test
    @DisplayName("토큰 재발급")
    void refreshTokens(){
        // given
        String refreshToken = "refreshToken";

        UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setKakaoId(1L);
        userResponse.setNickName("황주환");
        userResponse.setProfileImage("이미지");
        userResponse.setThumbnailImage("이미지");
        userResponse.setRefreshToken(refreshToken);
        userResponse.setGender("male");
        userResponse.setAgeRange("10~19");
        userResponse.setStatus("ACTIVE");
        userResponse.setRole("USER");

        given(userServiceClient.findUserById(any()))
                .willReturn(userResponse);

        CreateTokenResult createTokenResult = new CreateTokenResult();
        createTokenResult.setAccessToken("Access토큰");
        createTokenResult.setRefreshToken("Refresh토큰");

        given(jwtTokenProvider.refresh(any(),any()))
                .willReturn(createTokenResult);

        willDoNothing()
                .given(kafkaRefreshTokenCreateMessageSender)
                .send(any());

        // when
        CreateTokenResult result = authServiceImpl.refresh(refreshToken,1L);

        // then
        assertThat(result.getAccessToken()).isEqualTo(createTokenResult.getAccessToken());
        assertThat(result.getRefreshToken()).isEqualTo(createTokenResult.getRefreshToken());
    }

    @Test
    @DisplayName("로그아웃")
    void logout() {
        // given
        willDoNothing()
                .given(kafkaLogoutMessageSender)
                .send(any());

        // when
        authServiceImpl.logout(1L);

        // then
        then(kafkaLogoutMessageSender).should(times(1)).send(any());
    }


}
