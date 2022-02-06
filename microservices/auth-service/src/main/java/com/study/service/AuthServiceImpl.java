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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.study.exception.NotFoundException.AUTH_NOT_FOUND;

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

        String accessToken = jwtUtils.createToken(user.getUserId(), user.getRole(), user.getNickName(), accessTokenExpirationTime);
        String refreshToken = jwtUtils.createToken(user.getUserId(), user.getRole(), user.getNickName(), refreshTokenExpirationTime);

        Auth auth = Auth.createAuth(user.getUserId(), refreshToken, 30L);
        authRepository.save(auth);
        return new TokenResponse(accessToken, refreshToken);
    }

    @Override
    public TokenResponse refresh(Long userId, String refreshToken) {
        Auth findAuth = authRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(AUTH_NOT_FOUND));

        if (!refreshToken.equals(findAuth.getRefreshToken())) {
            throw new NotMatchException("refreshToken 이 일치하지 않습니다.");
        }

        String accessToken = jwtUtils.createToken(refreshToken, accessTokenExpirationTime);

        String newRefreshToken = null;
        if (findAuth.getExpiration() < 7) {
            newRefreshToken = jwtUtils.createToken(refreshToken, refreshTokenExpirationTime);

            Auth auth = Auth.createAuth(userId, newRefreshToken, 30L);
            authRepository.save(auth);
        }
        return new TokenResponse(accessToken, newRefreshToken);
    }
}
