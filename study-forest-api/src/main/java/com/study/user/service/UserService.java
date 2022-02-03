package com.study.user.service;

import com.study.area.Area;
import com.study.area.AreaRepository;
import com.study.client.AwsClient;
import com.study.client.KakaoClient;
import com.study.client.KakaoProfile;
import com.study.common.DuplicateException;
import com.study.common.NotFoundException;
import com.study.security.util.JwtUtil;
import com.study.user.Role;
import com.study.user.User;
import com.study.user.UserRepository;
import com.study.user.dto.UserResponse;
import com.study.user.dto.UserUpdateDistanceRequest;
import com.study.user.dto.UserUpdateProfileRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.study.common.NotFoundException.AREA_NOT_FOUND;
import static com.study.common.NotFoundException.USER_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AreaRepository areaRepository;
    private final KakaoClient kakaoClient;
    private final AwsClient awsClient;
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

    public String uploadImage(MultipartFile image) {
        return awsClient.upload(image);
    }

    @Transactional
    public void updateProfile(Long userId, UserUpdateProfileRequest request) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        findUser.changProfile(request.getNickName(), request.getImageUrl());
    }

    public UserResponse findById(Long userId) {
        User findUser = userRepository.findWithAreaById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
        return UserResponse.from(findUser);
    }

    @Transactional
    public void updateArea(Long userId, Long areaId) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        Area findArea = areaRepository.findById(areaId)
                .orElseThrow(() -> new NotFoundException(AREA_NOT_FOUND));

        findUser.changeArea(findArea);
    }

    @Transactional
    public void updateDistance(Long userId, UserUpdateDistanceRequest request) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        findUser.changeDistance(request.getDistance());
    }
}
