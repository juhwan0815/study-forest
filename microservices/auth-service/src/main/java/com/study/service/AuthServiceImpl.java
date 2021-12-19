package com.study.service;

import com.study.client.KakaoClient;
import com.study.client.UserServiceClient;
import com.study.dto.KakaoProfile;
import com.study.dto.TokenResponse;
import com.study.dto.UserResponse;
import com.study.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final KakaoClient kakaoClient;
    private final UserServiceClient userServiceClient;
    private final JwtUtils jwtUtils;

    @Value("${token.access_token.expiration_time}")
    private String accessTokenExpirationTime;

    @Value("${token.refresh_token.expiration_time}")
    private String refreshTokenExpirationTime;

    @Override
    public TokenResponse login(String kakaoToken, String fcmToken) {
        KakaoProfile kakaoProfile = kakaoClient.getKakaoProfile(kakaoToken);

        UserResponse user = userServiceClient.findUserByKakaoId(kakaoProfile.getId());

        String accessToken = jwtUtils.createToken(user.getId(), user.getRole(), user.getNickName(), Long.valueOf(accessTokenExpirationTime));
        String refreshToken = jwtUtils.createToken(user.getId(), user.getRole(), user.getNickName(), Long.valueOf(refreshTokenExpirationTime));

        return new TokenResponse(accessToken, refreshToken);
    }
}
