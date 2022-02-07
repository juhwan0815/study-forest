package com.study.service;

import com.study.client.AwsClient;
import com.study.client.KakaoClient;
import com.study.client.KakaoProfile;
import com.study.domain.User;
import com.study.domain.UserRole;
import com.study.dto.keyword.KeywordCreateRequest;
import com.study.dto.keyword.KeywordResponse;
import com.study.dto.user.UserFindRequest;
import com.study.dto.user.UserResponse;
import com.study.dto.user.UserUpdateDistanceRequest;
import com.study.dto.user.UserUpdateRequest;
import com.study.exception.DuplicateException;
import com.study.exception.NotFoundException;
import com.study.repository.UserQueryRepository;
import com.study.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import static com.study.exception.NotFoundException.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final KakaoClient kakaoClient;
    private final AwsClient awsClient;

    private final UserRepository userRepository;
    private final UserQueryRepository userQueryRepository;

    @Override
    @Transactional
    public void create(String kakaoToken) {
        KakaoProfile kakaoProfile = kakaoClient.getKakaoProfile(kakaoToken);

        if (userRepository.findByKakaoId(kakaoProfile.getId()).isPresent()) {
            throw new DuplicateException("이미 가입한 회원입니다.");
        }

        User user = User.createUser(kakaoProfile.getId(),
                kakaoProfile.getProperties().getNickname(),
                kakaoProfile.getKakao_account().getAge_range(),
                kakaoProfile.getKakao_account().getGender(),
                kakaoProfile.getProperties().getThumbnail_image(),
                UserRole.USER);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserResponse findByKakaoId(Long kakaoId, String fcmToken) {
        User findUser = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        findUser.changeFcmToken(fcmToken);
        return UserResponse.from(findUser);
    }

    @Override
    public UserResponse findById(Long userId) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
        return UserResponse.from(findUser);
    }

    @Override
    @Transactional
    public void update(Long userId, UserUpdateRequest request) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        findUser.changeProfile(request.getNickName(), request.getImageUrl());
    }

    @Override
    @Transactional
    public void updateArea(Long userId, Long areaId) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        findUser.changeArea(areaId);
    }

    @Override
    @Transactional
    public void updateDistance(Long userId, UserUpdateDistanceRequest request) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        findUser.changeDistance(request.getDistance());
    }

    @Override
    @Transactional
    public void addKeyword(Long userId, KeywordCreateRequest request) {
        User findUser = userRepository.findWithKeywordById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
        findUser.addKeyword(request.getContent());
    }

    @Override
    @Transactional
    public void deleteKeyword(Long userId, Long keywordId) {
        User findUser = userRepository.findWithKeywordById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
        findUser.deleteKeyword(keywordId);
    }


    @Override
    public List<KeywordResponse> findKeywordById(Long userId) {
        User findUser = userRepository.findWithKeywordById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
        return findUser.getKeywords().stream()
                .map(KeywordResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> findByIdIn(UserFindRequest request) {
        List<User> users = userRepository.findByIdIn(request.getUserIds());
        return users.stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> findByKeywordContentContain(String studyName) {
        return userQueryRepository.findByKeywordContentContain(studyName);
    }

    @Override
    public String uploadImage(MultipartFile image) {
        return awsClient.upload(image);
    }

}
