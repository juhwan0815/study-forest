package com.study.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.study.client.KakaoClient;
import com.study.domain.Image;
import com.study.domain.User;
import com.study.domain.UserRole;
import com.study.dto.KakaoProfile;
import com.study.dto.UserResponse;
import com.study.dto.UserUpdateDistanceRequest;
import com.study.dto.UserUpdateNickNameRequest;
import com.study.repository.UserQueryRepository;
import com.study.repository.UserRepository;
import com.study.util.ImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final AmazonS3Client amazonS3Client;
    private final KakaoClient kakaoClient;
    private final UserRepository userRepository;
    private final UserQueryRepository userQueryRepository;
    private final ImageUtil imageUtil;

    @Override
    @Transactional
    public UserResponse create(String kakaoToken) {
        KakaoProfile kakaoProfile = kakaoClient.getKakaoProfile(kakaoToken);

        if (userRepository.existsByKakaoId(kakaoProfile.getId())) {
            throw new RuntimeException("");
        }

        User user = User.createUser(kakaoProfile.getId(),
                kakaoProfile.getProperties().getNickname(),
                kakaoProfile.getKakao_account().getGender(),
                kakaoProfile.getKakao_account().getAge_range(), UserRole.USER);
        user.changeImage(Image.createImage(kakaoProfile.getProperties().getThumbnail_image(), null));
        userRepository.save(user);

        return UserResponse.from(user);
    }

    @Override
    @Transactional
    public UserResponse findByKakaoId(Long kakaoId, String fcmToken) {
        User findUser = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new RuntimeException(""));

        findUser.changeFcmToken(fcmToken);
        return UserResponse.from(findUser);
    }

    @Override
    public UserResponse findById(Long userId) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException());
        return UserResponse.from(findUser);
    }

    @Override
    @Transactional
    public UserResponse updateImage(Long userId, MultipartFile image) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(""));

        Image updateImage = imageUtil.uploadImage(image, findUser.getImage().getImageStoreName());
        findUser.changeImage(updateImage);
        return UserResponse.from(findUser);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(Long userId, UserUpdateNickNameRequest request) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(""));

        findUser.changeProfile(request.getNickName());
        return UserResponse.from(findUser);
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(""));
        userRepository.delete(findUser);

        // TODO 회원탈퇴 Kafka
    }

    @Override
    @Transactional
    public UserResponse updateArea(Long userId, Long areaId) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(""));

        findUser.changeArea(areaId);
        return UserResponse.from(findUser);
    }

    @Override
    @Transactional
    public UserResponse updateDistance(Long userId, UserUpdateDistanceRequest request) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(""));

        findUser.changeDistance(request.getDistance());
        return UserResponse.from(findUser);
    }

}
