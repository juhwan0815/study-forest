package com.study.userservice.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.study.userservice.domain.User;
import com.study.userservice.model.user.UserResponse;
import com.study.userservice.repository.UserRepository;
import com.study.userservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URL;
import java.util.Optional;

import static com.study.userservice.UserFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AmazonS3Client amazonS3Client;

    @Test
    @DisplayName("신규회원이 회원로그인한다.")
    void loginNewUser(){
        // given
        given(userRepository.findByKakaoId(anyLong()))
                .willReturn(Optional.empty());

        given(userRepository.save(any()))
                .willReturn(TEST_USER);

        // when
        UserResponse userResponse = userService.create(TEST_USER_LOGIN_REQUEST);

        // then
        assertThat(userResponse.getId()).isEqualTo(TEST_USER.getId());
        assertThat(userResponse.getKakaoId()).isEqualTo(TEST_USER_LOGIN_REQUEST.getKakaoId());
        assertThat(userResponse.getNickName()).isEqualTo(TEST_USER_LOGIN_REQUEST.getNickName());
        assertThat(userResponse.getImage().getProfileImage()).isEqualTo(TEST_USER_LOGIN_REQUEST.getProfileImage());
        assertThat(userResponse.getImage().getThumbnailImage()).isEqualTo(TEST_USER_LOGIN_REQUEST.getThumbnailImage());
        assertThat(userResponse.getGender()).isEqualTo(TEST_USER_LOGIN_REQUEST.getGender());
        assertThat(userResponse.getAgeRange()).isEqualTo(TEST_USER_LOGIN_REQUEST.getAgeRange());
        assertThat(userResponse.getNumberOfStudyApply()).isEqualTo(0);

        then(userRepository).should(times(1)).findByKakaoId(anyLong());
        then(userRepository).should(times(1)).save(any());
    }

    @Test
    @DisplayName("기존회원이 로그인을 한다.")
    void loginUser(){
        // given
        given(userRepository.findByKakaoId(anyLong()))
                .willReturn(Optional.of(TEST_USER));

        // when
        UserResponse userResponse = userService.create(TEST_USER_LOGIN_REQUEST);

        // then
        assertThat(userResponse.getId()).isEqualTo(TEST_USER.getId());
        assertThat(userResponse.getKakaoId()).isEqualTo(TEST_USER_LOGIN_REQUEST.getKakaoId());
        assertThat(userResponse.getNickName()).isEqualTo(TEST_USER_LOGIN_REQUEST.getNickName());
        assertThat(userResponse.getImage().getProfileImage()).isEqualTo(TEST_USER_LOGIN_REQUEST.getProfileImage());
        assertThat(userResponse.getImage().getThumbnailImage()).isEqualTo(TEST_USER_LOGIN_REQUEST.getThumbnailImage());
        assertThat(userResponse.getGender()).isEqualTo(TEST_USER_LOGIN_REQUEST.getGender());
        assertThat(userResponse.getAgeRange()).isEqualTo(TEST_USER_LOGIN_REQUEST.getAgeRange());
        assertThat(userResponse.getNumberOfStudyApply()).isEqualTo(0);

        then(userRepository).should(times(1)).findByKakaoId(anyLong());
        then(userRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("회원 프로필의 이미지와 닉네임을 변경한다.")
    void changeImageAndName() throws Exception {
        // given
        User user = createTestUser();

        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        willDoNothing()
                .given(amazonS3Client)
                .deleteObject(any(),any());

        given(amazonS3Client.putObject(any()))
                .willReturn(null);

        given(amazonS3Client.getUrl(any(),any()))
                .willReturn(new URL("http:이미지"))
                .willReturn(new URL("http:썸네일이미지"));

        // when
        UserResponse userResponse =
                userService.updateProfile(1L,TEST_IMAGE_FILE, TEST_USER_PROFILE_UPDATE_REQUEST1);

        // then
        assertThat(userResponse.getNickName()).isEqualTo(TEST_USER_PROFILE_UPDATE_REQUEST1.getNickName());
        assertThat(userResponse.getImage().getProfileImage()).isEqualTo("http:이미지");
        assertThat(userResponse.getImage().getThumbnailImage()).isEqualTo("http:썸네일이미지");

        then(userRepository).should(times(1)).findById(anyLong());
        then(amazonS3Client).should(times(2)).deleteObject(any(),any());
        then(amazonS3Client).should(times(1)).putObject(any());
        then(amazonS3Client).should(times(2)).getUrl(any(),any());
    }

    @Test
    @DisplayName("회원 프로필의 이미지를 삭제하고 이름만 변경한다.")
    void changeNullImageAndName(){
        // given
        User user = createTestUser();

        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        willDoNothing()
                .given(amazonS3Client)
                .deleteObject(any(),any());

        // when
        UserResponse userResponse = userService.updateProfile(1L,TEST_EMPTY_IMAGE_FILE,
                                                             TEST_USER_PROFILE_UPDATE_REQUEST2);

        // then
        assertThat(userResponse.getNickName()).isEqualTo(TEST_USER_PROFILE_UPDATE_REQUEST2.getNickName());
        assertThat(userResponse.getImage()).isNull();

        then(userRepository).should(times(1)).findById(anyLong());
        then(amazonS3Client).should(times(2)).deleteObject(any(),any());
    }

    @Test
    @DisplayName("회원 프로필의 이름만 변경한다.")
    void changeName(){
        // given
        User user = createTestUser();

        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        // when
        UserResponse userResponse = userService.updateProfile(1L,TEST_EMPTY_IMAGE_FILE,
                TEST_USER_PROFILE_UPDATE_REQUEST1);

        // then
        assertThat(userResponse.getNickName()).isEqualTo(TEST_USER_PROFILE_UPDATE_REQUEST1.getNickName());
        assertThat(userResponse.getImage()).isNotNull();

        then(userRepository).should(times(1)).findById(anyLong());;
    }


}
