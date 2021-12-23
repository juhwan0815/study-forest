package com.study.service;

import com.study.client.KakaoClient;
import com.study.client.KakaoProfile;
import com.study.domain.Keyword;
import com.study.domain.User;
import com.study.domain.UserRole;
import com.study.dto.keyword.KeywordResponse;
import com.study.dto.user.UserResponse;
import com.study.kafka.sender.UserDeleteMessageSender;
import com.study.repository.UserQueryRepository;
import com.study.repository.UserRepository;
import com.study.util.ImageUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.convert.DurationFormat;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static com.study.UserFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private KakaoClient kakaoClient;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserQueryRepository userQueryRepository;

    @Mock
    private ImageUtil imageUtil;

    @Mock
    private UserDeleteMessageSender userDeleteMessageSender;

    @Test
    @DisplayName("회원 가입을 한다.")
    void create() {
        // given
        KakaoProfile.Properties properties =
                new KakaoProfile.Properties("황주환", "이미지", "이미지");

        KakaoProfile.KakaoAccount kakaoAccount =
                new KakaoProfile.KakaoAccount("10~19", "male");

        KakaoProfile kakaoProfile = new KakaoProfile(1L, properties, kakaoAccount);

        given(kakaoClient.getKakaoProfile(any())).
                willReturn(kakaoProfile);

        given(userRepository.findByKakaoId(any()))
                .willReturn(Optional.empty());

        given(userRepository.save(any()))
                .willReturn(null);

        // when
        UserResponse result = userService.create("kakaoToken");

        // then
        assertThat(result.getNickName()).isEqualTo(kakaoProfile.getProperties().getNickname());
        assertThat(result.getAgeRange()).isEqualTo(kakaoProfile.getKakao_account().getAge_range());
        assertThat(result.getGender()).isEqualTo(kakaoProfile.getKakao_account().getGender());
        assertThat(result.getRole()).isEqualTo(TEST_USER.getRole());
        assertThat(result.getDistance()).isEqualTo(TEST_USER.getDistance());
        assertThat(result.getImageUrl()).isEqualTo(kakaoProfile.getProperties().getProfile_image());
        assertThat(result.getFcmToken()).isNull();
        assertThat(result.getAreaId()).isNull();

        then(kakaoClient).should(times(1)).getKakaoProfile(any());
        then(userRepository).should(times(1)).findByKakaoId(any());
        then(userRepository).should(times(1)).save(any());
    }

    @Test
    @DisplayName("예외 테스트 : 회원 가입을 하는데 이미 가입한 경우 예외가 발생한다.")
    void createDuplicateUser() {
        // given
        KakaoProfile.Properties properties =
                new KakaoProfile.Properties("황주환", "이미지", "이미지");

        KakaoProfile.KakaoAccount kakaoAccount =
                new KakaoProfile.KakaoAccount("10~19", "male");

        KakaoProfile kakaoProfile = new KakaoProfile(1L, properties, kakaoAccount);

        given(kakaoClient.getKakaoProfile(any())).
                willReturn(kakaoProfile);

        given(userRepository.findByKakaoId(any()))
                .willReturn(Optional.of(TEST_USER));

        // when
        assertThrows(RuntimeException.class, () -> userService.create("kakaoToken"));

        // then
        then(kakaoClient).should(times(1)).getKakaoProfile(any());
        then(userRepository).should(times(1)).findByKakaoId(any());
    }

    @Test
    @DisplayName("카카오 ID 로 조회하고 FCM 토큰을 변경한다.")
    void findByKakaoId() {
        // given
        User user = User.createUser(1L, "황주환", "10~19", "male", UserRole.USER);

        given(userRepository.findByKakaoId(any()))
                .willReturn(Optional.of(user));

        // when
        UserResponse result = userService.findByKakaoId(1L, "fcmToken");

        // then
        assertThat(result.getFcmToken()).isEqualTo("fcmToken");
        then(userRepository).should(times(1)).findByKakaoId(any());
    }

    @Test
    @DisplayName("회원 ID 로 조회한다.")
    void findById() {

        // given
        given(userRepository.findById(any()))
                .willReturn(Optional.of(TEST_USER));

        // when
        UserResponse result = userService.findById(1L);

        // then
        assertThat(result).isNotNull();
        then(userRepository).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("회원 이미지를 변경한다.")
    void updateImage() {
        // given
        User user = User.createUser(1L, "황주환", "10~19", "male", UserRole.USER);

        MockMultipartFile image = new MockMultipartFile("image", "프로필사진.png",
                MediaType.IMAGE_PNG_VALUE, "<<image>>".getBytes(StandardCharsets.UTF_8));

        given(userRepository.findById(any()))
                .willReturn(Optional.of(user));

        given(imageUtil.uploadImage(any(), any()))
                .willReturn(TEST_IMAGE);

        // when
        UserResponse result = userService.updateImage(1L, image);

        // then
        assertThat(result.getImageUrl()).isEqualTo(TEST_IMAGE.getImageUrl());
        then(userRepository).should(times(1)).findById(any());
        then(imageUtil).should(times(1)).uploadImage(any(), any());
    }

    @Test
    @DisplayName("회원의 프로필을 변경한다.")
    void updateProfile() {
        // given
        User user = User.createUser(1L, "황주환", "10~19", "male", UserRole.USER);

        given(userRepository.findById(any()))
                .willReturn(Optional.of(user));

        // when
        UserResponse result = userService.updateProfile(1L, TEST_USER_UPDATE_NICKNAME_REQUEST);

        // then
        assertThat(result.getNickName()).isEqualTo(TEST_USER_UPDATE_NICKNAME_REQUEST.getNickName());
        then(userRepository).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("회원을 탈퇴한다.")
    void delete() {
        // given
        given(userRepository.findById(any()))
                .willReturn(Optional.of(TEST_USER));

        willDoNothing()
                .given(userRepository)
                .delete(any());

        willDoNothing()
                .given(userDeleteMessageSender)
                .send(any());

        // when
        userService.delete(1L);

        // then
        then(userRepository).should(times(1)).findById(any());
        then(userRepository).should(times(1)).delete(any());
        then(userDeleteMessageSender).should(times(1)).send(any());
    }

    @Test
    @DisplayName("회원의 지역을 변경한다.")
    void updateArea() {
        // given
        User user = User.createUser(1L, "황주환", "10~19", "male", UserRole.USER);

        given(userRepository.findById(any()))
                .willReturn(Optional.of(user));

        // when
        UserResponse result = userService.updateArea(1L, 1L);

        // then
        assertThat(result.getAreaId()).isEqualTo(1L);
        then(userRepository).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("회원의 검색거리를 변경한다.")
    void updateDistance() {
        // given
        User user = User.createUser(1L, "황주환", "10~19", "male", UserRole.USER);

        given(userRepository.findById(any()))
                .willReturn(Optional.of(user));

        // when
        UserResponse result = userService.updateDistance(1L, TEST_USER_UPDATE_DISTANCE_REQUEST);

        // then
        assertThat(result.getDistance()).isEqualTo(TEST_USER_UPDATE_DISTANCE_REQUEST.getDistance());
        then(userRepository).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("회원의 관심 키워드를 추가한다.")
    void addKeyword() {
        // given
        User user = User.createUser(1L, "황주환", "10~19", "male", UserRole.USER);

        given(userQueryRepository.findWithKeywordById(any()))
                .willReturn(user);

        // when
        userService.addKeyword(1L, TEST_KEYWORD_CREATE_REQUEST);

        // then
        assertThat(user.getKeywords().size()).isEqualTo(1);
        then(userQueryRepository).should(times(1)).findWithKeywordById(any());
    }

    @Test
    @DisplayName("회원의 관심 키워드를 삭제한다.")
    void deleteKeyword() {
        // given
        User user = User.createUser(1L, "황주환", "10~19", "male", UserRole.USER);
        user.getKeywords().add(TEST_KEYWORD);

        given(userQueryRepository.findWithKeywordById(any()))
                .willReturn(user);

        // when
        userService.deleteKeyword(1L, 1L);

        // then
        assertThat(user.getKeywords().size()).isEqualTo(0);
        then(userQueryRepository).should(times(1)).findWithKeywordById(any());
    }

    @Test
    @DisplayName("회원의 관심 키워드를 조회한다.")
    void findKeywordById() {
        // given
        User user = User.createUser(1L, "황주환", "10~19", "male", UserRole.USER);
        user.getKeywords().add(TEST_KEYWORD);

        // when
        given(userQueryRepository.findWithKeywordById(any()))
                .willReturn(user);

        List<KeywordResponse> result = userService.findKeywordById(1L);

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getKeywordId()).isEqualTo(TEST_KEYWORD.getId());
        assertThat(result.get(0).getContent()).isEqualTo(TEST_KEYWORD.getContent());
        then(userQueryRepository).should(times(1)).findWithKeywordById(any());
    }
}