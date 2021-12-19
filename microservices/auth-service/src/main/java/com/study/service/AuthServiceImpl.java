package com.study.service;

import com.study.client.KakaoClient;
import com.study.client.UserServiceClient;
import com.study.dto.KakaoProfile;
import com.study.dto.TokenResponse;
import com.study.dto.UserResponse;
import com.study.repository.AuthRepository;
import com.study.utils.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final KakaoClient kakaoClient;
    private final UserServiceClient userServiceClient;

    private final JwtUtils jwtUtils;

    private final AuthRepository authRepository;

    @Value("${token.access_token.expiration_time}")
    private String accessTokenExpirationTime;

    @Value("${token.refresh_token.expiration_time}")
    private String refreshTokenExpirationTime;

    @Override
    public TokenResponse login(String kakaoToken, String fcmToken) {
        KakaoProfile kakaoProfile = kakaoClient.getKakaoProfile(kakaoToken);

        UserResponse user = userServiceClient.loginByKakaoId(kakaoProfile.getId(), fcmToken);

        String accessToken = jwtUtils.createToken(user.getUserId(), user.getRole(), user.getNickName(), Long.valueOf(accessTokenExpirationTime));
        String refreshToken = jwtUtils.createToken(user.getUserId(), user.getRole(), user.getNickName(), Long.valueOf(refreshTokenExpirationTime));

        authRepository.saveRefreshToken(String.valueOf(user.getUserId()), refreshToken);
        return new TokenResponse(accessToken, refreshToken);
    }

    @Override
    public TokenResponse refresh(Long userId, String refreshToken) {
        String saveRefreshToken = authRepository.findRefreshTokenByUserId(String.valueOf(userId));

        if(!refreshToken.equals(saveRefreshToken)){
            throw new RuntimeException();
        }

        String accessToken = jwtUtils.createToken(userId, jwtUtils.getRole(refreshToken), jwtUtils.getNickName(refreshToken), Long.valueOf(accessTokenExpirationTime));

        String newRefreshToken = null;
        if (authRepository.getExpireIn7Days(String.valueOf(userId))){
            newRefreshToken = jwtUtils.createToken(userId, jwtUtils.getRole(refreshToken), jwtUtils.getNickName(refreshToken), Long.valueOf(refreshTokenExpirationTime));
            authRepository.saveRefreshToken(String.valueOf(userId), newRefreshToken);
        }
        return new TokenResponse(accessToken, newRefreshToken);
    }
}
