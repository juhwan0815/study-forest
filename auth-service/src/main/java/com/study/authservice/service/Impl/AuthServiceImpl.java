package com.study.authservice.service.Impl;

import com.study.authservice.client.KakaoServiceClient;
import com.study.authservice.client.UserServiceClient;
import com.study.authservice.domain.Auth;
import com.study.authservice.exception.AuthException;
import com.study.authservice.model.common.CreateTokenResult;
import com.study.authservice.model.common.KakaoProfile;
import com.study.authservice.model.user.UserLoginRequest;
import com.study.authservice.model.user.UserResponse;
import com.study.authservice.repository.AuthRepository;
import com.study.authservice.service.AuthService;
import com.study.authservice.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthRepository authRepository;
    private final KakaoServiceClient kakaoServiceClient;
    private final UserServiceClient userServiceClient;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public CreateTokenResult login(String kakaoToken,String fcmToken) {
        KakaoProfile kakaoProfile = kakaoServiceClient.getKakaoProfile(kakaoToken);

        // TODO Circuit Breaker 적용하기
        UserResponse user = userServiceClient.login(UserLoginRequest.from(kakaoProfile,fcmToken));

        CreateTokenResult createTokenResult = jwtTokenProvider.createToken(user.getId(),user.getRole(),user.getNickName());

        Optional<Auth> findAuth = authRepository.findByUserId(user.getId());

        if(findAuth.isPresent()){
            Auth auth = findAuth.get();
            auth.changeRefreshToken(createTokenResult.getRefreshToken());
        } else {
            authRepository.save(Auth.createAuth(user.getId(), createTokenResult.getRefreshToken()));
        }

        return createTokenResult;
    }

    @Override
    public String refresh(String refreshToken,Long userId) {
        Auth findAuth = authRepository.findByUserId(userId)
                .orElseThrow(() -> new AuthException(userId + "는 존재하지 않는 회원 ID입니다."));

        String refresh = jwtTokenProvider.refresh(refreshToken.substring(7), findAuth);
        return refresh;
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        Auth findAuth = authRepository.findByUserId(userId)
                .orElseThrow(() -> new AuthException(userId + "는 존재하지 않는 회원 ID입니다."));

        authRepository.delete(findAuth);
    }


}
