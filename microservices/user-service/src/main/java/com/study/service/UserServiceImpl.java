package com.study.service;

import com.study.client.KakaoClient;
import com.study.client.KakaoClientImpl;
import com.study.domain.Image;
import com.study.domain.User;
import com.study.domain.UserRole;
import com.study.dto.KakaoProfile;
import com.study.dto.UserResponse;
import com.study.repository.UserQueryRepository;
import com.study.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final KakaoClient kakaoClient;
    private final UserRepository userRepository;
    private final UserQueryRepository userQueryRepository;

    @Override
    public UserResponse create(String kakaoToken) {
        KakaoProfile kakaoProfile = kakaoClient.getKakaoProfile(kakaoToken);

        if (userRepository.existsByKakaoId(kakaoProfile.getId())) {
            throw new RuntimeException("");
        }

        User user = User.createUser(kakaoProfile.getId(), kakaoProfile.getProperties().getNickname(),
                                    kakaoProfile.getKakao_account().getAge_range(), UserRole.USER);
        user.changeImage(Image.createImage(kakaoProfile.getProperties().getThumbnail_image(), null));
        userRepository.save(user);

        return UserResponse.from(user);
    }
}
