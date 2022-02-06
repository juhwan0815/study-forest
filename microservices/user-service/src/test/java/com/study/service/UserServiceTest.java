package com.study.service;

import com.study.client.AwsClient;
import com.study.client.KakaoClient;
import com.study.client.KakaoProfile;
import com.study.domain.User;
import com.study.dto.keyword.KeywordResponse;
import com.study.dto.user.UserResponse;
import com.study.exception.DuplicateException;
import com.study.exception.NotFoundException;
import com.study.repository.UserQueryRepository;
import com.study.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.study.UserFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private KakaoClient kakaoClient;

    @Mock
    private AwsClient awsClient;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserQueryRepository userQueryRepository;

    @Test
    @DisplayName("회원 가입을 한다.")
    void create() {
        // given
        KakaoProfile.Properties properties =
                new KakaoProfile.Properties("황주환", "imageUrl", "imageUrl");

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
        userService.create(TEST_KAKAO_TOKEN);

        // then
        then(kakaoClient).should(times(1)).getKakaoProfile(any());
        then(userRepository).should(times(1)).findByKakaoId(any());
        then(userRepository).should(times(1)).save(any());
    }

    @Test
    @DisplayName("회원 가입을 하는데 이미 가입한 경우 예외가 발생한다.")
    void createDuplicate() {
        // given
        KakaoProfile.Properties properties =
                new KakaoProfile.Properties("황주환", "imageUrl", "imageUrl");

        KakaoProfile.KakaoAccount kakaoAccount =
                new KakaoProfile.KakaoAccount("10~19", "male");

        KakaoProfile kakaoProfile = new KakaoProfile(1L, properties, kakaoAccount);

        given(kakaoClient.getKakaoProfile(any())).
                willReturn(kakaoProfile);

        given(userRepository.findByKakaoId(any()))
                .willReturn(Optional.of(TEST_USER));

        // when
        assertThrows(DuplicateException.class, () -> userService.create(TEST_KAKAO_TOKEN));

        // then
        then(kakaoClient).should(times(1)).getKakaoProfile(any());
        then(userRepository).should(times(1)).findByKakaoId(any());
    }

    @Test
    @DisplayName("카카오 ID 로 조회하고 FCM 토큰을 변경한다.")
    void findByKakaoId() {
        // given
        given(userRepository.findByKakaoId(any()))
                .willReturn(Optional.of(TEST_USER));

        // when
        UserResponse result = userService.findByKakaoId(TEST_USER_KAKAO_ID, TEST_FCM_TOKEN);

        // then
        assertThat(result.getFcmToken()).isEqualTo(TEST_FCM_TOKEN);
        then(userRepository).should(times(1)).findByKakaoId(any());
    }

    @Test
    @DisplayName("카카오 ID 로 조회할 때 회원이 존재하지 않으면 예외가 발생한다.")
    void findByKakaoIdNotFound() {
        // given
        given(userRepository.findByKakaoId(any()))
                .willReturn(Optional.empty());

        // when
        assertThrows(NotFoundException.class, () -> userService.findByKakaoId(TEST_USER_KAKAO_ID, TEST_FCM_TOKEN));

        // then
        then(userRepository).should(times(1)).findByKakaoId(any());
    }

    @Test
    @DisplayName("회원을 조회한다.")
    void findById() {
        // given
        given(userRepository.findById(any()))
                .willReturn(Optional.of(TEST_USER));

        // when
        UserResponse result = userService.findById(TEST_USER.getId());

        // then
        assertThat(result.getUserId()).isEqualTo(TEST_USER.getId());
        assertThat(result.getNickName()).isEqualTo(TEST_USER.getNickName());
        assertThat(result.getAgeRange()).isEqualTo(TEST_USER.getAgeRange());
        assertThat(result.getGender()).isEqualTo(TEST_USER.getGender());
        assertThat(result.getImageUrl()).isEqualTo(TEST_USER.getImageUrl());
        assertThat(result.getDistance()).isEqualTo(TEST_USER.getDistance());
        assertThat(result.getFcmToken()).isEqualTo(TEST_USER.getFcmToken());
        assertThat(result.getAreaId()).isEqualTo(TEST_USER.getAreaId());
        then(userRepository).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("회원을 조회할 때 존재하지 않으면 예외가 발생한다.")
    void findByIdNotFound() {
        // given
        given(userRepository.findById(any()))
                .willReturn(Optional.empty());

        // when
        assertThrows(NotFoundException.class, () -> userService.findById(TEST_USER.getId()));

        // then
        then(userRepository).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("회원의 프로필을 변경한다.")
    void update() {
        // given
        given(userRepository.findById(any()))
                .willReturn(Optional.of(TEST_USER));

        // when
        userService.update(TEST_USER.getId(), TEST_USER_UPDATE_REQUEST);

        // then
        then(userRepository).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("회원의 프로필을 변경할 때 회원이 존재하지 않으면 예외가 발생한다.")
    void updateNotFound() {
        // given
        given(userRepository.findById(any()))
                .willReturn(Optional.empty());

        // when
        assertThrows(NotFoundException.class, () -> userService.update(TEST_USER.getId(), TEST_USER_UPDATE_REQUEST));

        // then
        then(userRepository).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("회원의 지역을 변경한다.")
    void updateArea() {
        // given
        given(userRepository.findById(any()))
                .willReturn(Optional.of(TEST_USER));

        // when
        userService.updateArea(TEST_USER.getId(), TEST_USER_AREA_ID);

        // then
        then(userRepository).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("회원의 지역을 변경할 때 회원이 존재하지 않으면 예외가 발생한다.")
    void updateAreaNotFound() {
        // given
        given(userRepository.findById(any()))
                .willReturn(Optional.empty());

        // when
        assertThrows(NotFoundException.class, () -> userService.updateArea(TEST_USER.getId(), TEST_USER_AREA_ID));

        // then
        then(userRepository).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("회원의 검색거리를 변경한다.")
    void updateDistance() {
        // given
        given(userRepository.findById(any()))
                .willReturn(Optional.of(TEST_USER));

        // when
        userService.updateDistance(TEST_USER.getId(), TEST_USER_UPDATE_DISTANCE_REQUEST);

        // then
        then(userRepository).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("회원의 검색거리를 변경할 때 존재하지 않으면 예외가 발생한다.")
    void updateDistanceNotFound() {
        // given
        given(userRepository.findById(any()))
                .willReturn(Optional.empty());

        // when
        assertThrows(NotFoundException.class, () -> userService.updateDistance(TEST_USER.getId(), TEST_USER_UPDATE_DISTANCE_REQUEST));

        // then
        then(userRepository).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("회원의 관심 키워드를 추가한다.")
    void addKeyword() {
        // given
        given(userRepository.findWithKeywordById(any()))
                .willReturn(Optional.of(TEST_USER));

        // when
        userService.addKeyword(TEST_USER.getId(), TEST_KEYWORD_CREATE_REQUEST);

        // then
        then(userRepository).should(times(1)).findWithKeywordById(any());
    }

    @Test
    @DisplayName("회원의 관심 키워드를 추가할 때 존재하지 않으면 예외가 발생한다.")
    void addKeywordNotFound() {
        // given
        given(userRepository.findWithKeywordById(any()))
                .willReturn(Optional.empty());

        // when
        assertThrows(NotFoundException.class, () -> userService.addKeyword(TEST_USER.getId(), TEST_KEYWORD_CREATE_REQUEST));

        // then
        then(userRepository).should(times(1)).findWithKeywordById(any());
    }

    @Test
    @DisplayName("회원의 관심 키워드를 삭제한다.")
    void deleteKeyword() {
        // given
        User user = User.createUser(TEST_USER_KAKAO_ID, TEST_USER_NICKNAME, TEST_USER_AGE_RANGE, TEST_USER_GENDER, TEST_USER_IMAGE_URL, TEST_USER_ROLE);
        user.getKeywords().add(TEST_KEYWORD);

        given(userRepository.findWithKeywordById(any()))
                .willReturn(Optional.of(user));

        // when
        userService.deleteKeyword(TEST_USER.getId(), TEST_KEYWORD.getId());

        // then
        then(userRepository).should(times(1)).findWithKeywordById(any());
    }

    @Test
    @DisplayName("회원의 관심 키워드를 삭제할 때 존재하지 않으면 예외가 발생한다.")
    void deleteKeywordNotFound() {
        // given
        given(userRepository.findWithKeywordById(any()))
                .willReturn(Optional.empty());

        // when
        assertThrows(NotFoundException.class, () -> userService.deleteKeyword(TEST_USER.getId(), TEST_KEYWORD.getId()));

        // then
        then(userRepository).should(times(1)).findWithKeywordById(any());
    }

    @Test
    @DisplayName("회원의 관심 키워드를 조회한다.")
    void findKeywordById() {
        // given
        User user = User.createUser(TEST_USER_KAKAO_ID, TEST_USER_NICKNAME, TEST_USER_AGE_RANGE, TEST_USER_GENDER, TEST_USER_IMAGE_URL, TEST_USER_ROLE);
        user.getKeywords().add(TEST_KEYWORD);

        given(userRepository.findWithKeywordById(any()))
                .willReturn(Optional.of(user));

        // when
        List<KeywordResponse> result = userService.findKeywordById(TEST_USER.getId());

        // then
        assertThat(result.size()).isEqualTo(1);
        then(userRepository).should(times(1)).findWithKeywordById(any());
    }

    @Test
    @DisplayName("회원의 관심 키워드를 조회할때 존재하지 않으면 예외가 발생한다.")
    void findKeywordByIdNotFound() {
        // given
        given(userRepository.findWithKeywordById(any()))
                .willReturn(Optional.empty());

        // when
        assertThrows(NotFoundException.class, () -> userService.findKeywordById(TEST_USER.getId()));

        // then
        then(userRepository).should(times(1)).findWithKeywordById(any());
    }

    @Test
    @DisplayName("회원 ID 리스트로 조회한다.")
    void findByIdIn() {
        // given
        given(userRepository.findByIdIn(any()))
                .willReturn(Arrays.asList(TEST_USER));

        // when
        List<UserResponse> result = userService.findByIdIn(TEST_USER_FIND_REQUEST);

        // then
        assertThat(result.size()).isEqualTo(1);
        then(userRepository).should(times(1)).findByIdIn(any());
    }

    @Test
    @DisplayName("검색어를 포함하는 키워드를 가진 회원을 조회한다.")
    void findByKeywordContentContain() {
        // given
        given(userQueryRepository.findByKeywordContentContain(any()))
                .willReturn(Arrays.asList(TEST_USER_RESPONSE));

        // when
        List<UserResponse> result = userService.findByKeywordContentContain("검색어");

        // then
        assertThat(result.size()).isEqualTo(1);
        then(userQueryRepository).should(times(1)).findByKeywordContentContain(any());
    }

    @Test
    @DisplayName("이미지를 업로드한다.")
    void uploadImage() {
        // given
        given(awsClient.upload(any()))
                .willReturn(TEST_USER_IMAGE_URL);
        // when
        String result = userService.uploadImage(TEST_IMAGE_FILE);

        // then
        assertThat(result).isEqualTo(TEST_USER_IMAGE_URL);
        then(awsClient).should(times(1)).upload(any());

    }
}