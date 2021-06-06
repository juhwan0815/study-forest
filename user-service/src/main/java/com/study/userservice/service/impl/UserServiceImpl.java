package com.study.userservice.service.impl;

import com.study.userservice.domain.User;
import com.study.userservice.domain.UserRole;
import com.study.userservice.exception.UserException;
import com.study.userservice.kafka.message.RefreshTokenCreateMessage;
import com.study.userservice.model.UserLoginRequest;
import com.study.userservice.model.UserResponse;
import com.study.userservice.repository.UserRepository;
import com.study.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserResponse save(UserLoginRequest request) {
        Optional<User> findUser = userRepository.findByKakaoId(request.getKakaoId());

        if (!findUser.isPresent()) {
            User savedUser = userRepository.save(
                    User.createUser(request.getKakaoId(),
                            request.getNickName(),
                            request.getThumbnailImage(),
                            request.getProfileImage(),
                            UserRole.USER));
            return UserResponse.from(savedUser);
        }

        return UserResponse.from(findUser.get());
    }

    @Override
    @Transactional
    public void updateRefreshToken(RefreshTokenCreateMessage refreshTokenCreateMessage) {
        try {
            User findUser = userRepository.findById(refreshTokenCreateMessage.getId())
                    .orElseThrow(() -> new UserException(refreshTokenCreateMessage.getId() + "는 존재하지 않는 회원입니다."));

            findUser.updateRefreshToken(refreshTokenCreateMessage.getRefreshToken());

        } catch (UserException ex) {
            log.error("{}", ex.getMessage());
        }

    }

    @Override
    public UserResponse findWithRefreshTokenById(Long userId) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("존재하지 않는 회원ID입니다."));

        return UserResponse.fromWithRefreshToken(findUser);
    }
}
