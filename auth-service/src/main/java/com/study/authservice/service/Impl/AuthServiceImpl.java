package com.study.authservice.service.Impl;

import com.study.authservice.client.KakaoServiceClient;
import com.study.authservice.client.UserServiceClient;
import com.study.authservice.kafka.message.LogoutMessage;
import com.study.authservice.kafka.message.RefreshTokenCreateMessage;
import com.study.authservice.kafka.sender.KafkaLogoutMessageSender;
import com.study.authservice.kafka.sender.KafkaRefreshTokenCreateMessageSender;
import com.study.authservice.model.CreateTokenResult;
import com.study.authservice.model.KakaoProfile;
import com.study.authservice.model.UserLoginRequest;
import com.study.authservice.model.UserResponse;
import com.study.authservice.service.AuthService;
import com.study.authservice.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final KakaoServiceClient kakaoServiceClient;
    private final UserServiceClient userServiceClient;
    private final JwtTokenProvider jwtTokenProvider;
    private final KafkaRefreshTokenCreateMessageSender kafkaRefreshTokenCreateMessageSender;
    private final KafkaLogoutMessageSender kafkaLogoutMessageSender;

    public CreateTokenResult login(String kakaoToken) {
        KakaoProfile kakaoProfile = kakaoServiceClient.getKakaoProfile(kakaoToken);

        // TODO Circuit Breaker 적용하기
        UserResponse user = userServiceClient.login(UserLoginRequest.from(kakaoProfile));

        CreateTokenResult createTokenResult = jwtTokenProvider.createToken(user);

        kafkaRefreshTokenCreateMessageSender
                .send(RefreshTokenCreateMessage.from(user.getId(), createTokenResult.getRefreshToken()));

        return createTokenResult;
    }

    @Override
    public CreateTokenResult refresh(String refreshToken,Long userId) {
        // TODO Circuit Breaker 적용하기
        UserResponse user = userServiceClient.findUserById(userId);

        CreateTokenResult createTokenResult = jwtTokenProvider.refresh(refreshToken.substring(7), user);

        kafkaRefreshTokenCreateMessageSender
                .send(RefreshTokenCreateMessage.from(user.getId(),createTokenResult.getRefreshToken()));

        return createTokenResult;
    }

    @Override
    public void logout(Long userId) {
        kafkaLogoutMessageSender.send(LogoutMessage.from(userId));
    }

}
