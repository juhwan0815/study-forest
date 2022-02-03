package com.study.user.service;

import com.study.client.KakaoClient;
import com.study.client.KakaoProfile;
import com.study.common.DuplicateException;
import com.study.common.NotFoundException;
import com.study.security.util.JwtUtil;
import com.study.user.Role;
import com.study.user.User;
import com.study.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.study.common.NotFoundException.USER_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final KakaoClient kakaoClient;
    private final JwtUtil jwtUtil;

    @Transactional
    public String create(String kakaoToken) {
        KakaoProfile kakaoProfile = kakaoClient.getKakaoProfile(kakaoToken);

        if (userRepository.findByKakaoId(kakaoProfile.getId()).isPresent()) {
            throw new DuplicateException("이미 회원가입한 회원입니다.");
        }

        User user = User.createUser(kakaoProfile.getId(),
                kakaoProfile.getProperties().getNickname(),
                kakaoProfile.getKakao_account().getAge_range(),
                kakaoProfile.getKakao_account().getGender(), Role.USER);
        user.changeImageUrl(kakaoProfile.getProperties().getThumbnail_image());
        userRepository.save(user);

        return jwtUtil.createToken(user.getId());
    }

    @Transactional
    public String login(String kakaoToken, String pushToken) {
        KakaoProfile kakaoProfile = kakaoClient.getKakaoProfile(kakaoToken);

        User findUser = userRepository.findByKakaoId(kakaoProfile.getId())
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
        findUser.changePushToken(pushToken);

        return jwtUtil.createToken(findUser.getId());
    }
}
