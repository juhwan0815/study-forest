package com.study.userservice.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.study.userservice.domain.User;
import com.study.userservice.domain.UserRole;
import com.study.userservice.kafka.message.LogoutMessage;
import com.study.userservice.kafka.message.RefreshTokenCreateMessage;
import com.study.userservice.model.UserLoginRequest;
import com.study.userservice.model.UserProfileUpdateRequest;
import com.study.userservice.model.UserResponse;
import com.study.userservice.repository.UserRepository;
import com.study.userservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AmazonS3Client amazonS3Client;

    @Test
    @DisplayName("회원 로그인 - 신규가입")
    void loginNewUser(){
        // given
        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setKakaoId(1L);
        userLoginRequest.setNickName("황주환");
        userLoginRequest.setProfileImage("이미지");
        userLoginRequest.setThumbnailImage("이미지");

        User user = User.createUser(1L, "황주환", "이미지",
                "이미지", UserRole.USER);

        given(userRepository.findByKakaoId(anyLong()))
                .willReturn(Optional.empty());

        given(userRepository.save(any()))
                .willReturn(user);

        // when
        UserResponse userResponse = userServiceImpl.save(userLoginRequest);

        // then
        assertThat(userResponse.getKakaoId()).isEqualTo(userLoginRequest.getKakaoId());
        assertThat(userResponse.getNickName()).isEqualTo(userLoginRequest.getNickName());
        assertThat(userResponse.getProfileImage()).isEqualTo(userLoginRequest.getProfileImage());
        assertThat(userResponse.getThumbnailImage()).isEqualTo(userLoginRequest.getThumbnailImage());

        then(userRepository).should(times(1)).findByKakaoId(anyLong());
        then(userRepository).should(times(1)).save(any());
    }

    @Test
    @DisplayName("회원 로그인 - 기존회원")
    void loginUser(){
        // given
        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setKakaoId(1L);
        userLoginRequest.setNickName("황주환");
        userLoginRequest.setProfileImage("이미지");
        userLoginRequest.setThumbnailImage("이미지");

        User user = User.createUser(1L, "황주환", "이미지",
                "이미지", UserRole.USER);

        given(userRepository.findByKakaoId(anyLong()))
                .willReturn(Optional.of(user));

        // when
        UserResponse userResponse = userServiceImpl.save(userLoginRequest);

        // then
        assertThat(userResponse.getKakaoId()).isEqualTo(userLoginRequest.getKakaoId());
        assertThat(userResponse.getNickName()).isEqualTo(userLoginRequest.getNickName());
        assertThat(userResponse.getProfileImage()).isEqualTo(userLoginRequest.getProfileImage());
        assertThat(userResponse.getThumbnailImage()).isEqualTo(userLoginRequest.getThumbnailImage());

        then(userRepository).should(times(1)).findByKakaoId(anyLong());
        then(userRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("Refresh 토큰 업데이트")
    void updateRefreshToken(){
        // given
        RefreshTokenCreateMessage refreshTokenCreateMessage = new RefreshTokenCreateMessage();
        refreshTokenCreateMessage.setId(1L);
        refreshTokenCreateMessage
                .setRefreshToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiUk9MRSI6IlVTRVIiLCJpYXQiOjE2MjI4ODU3NDEsImV4cCI6MTYyMzQ5MDU0MX0.c24V3JQxYlp9L4XgtFqfL6KR31CuTNRC5i-M0t8nMAU");

        User user = User.createUser(1L, "황주환", "이미지",
                "이미지", UserRole.USER);

        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        // when
        userServiceImpl.updateRefreshToken(refreshTokenCreateMessage);

        // then
        assertThat(user.getRefreshToken()).isEqualTo(refreshTokenCreateMessage.getRefreshToken());
    }

    @Test
    @DisplayName("회원 조회 (Refresh 토큰 포함)")
    void findWithRefreshTokenById(){
        // given
        User user = User.createUser(1L, "황주환", "이미지",
                "이미지", UserRole.USER);
        user.updateRefreshToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiUk9MRSI6IlVTRVIiLCJpYXQiOjE2MjI4ODU3NDEsImV4cCI6MTYyMzQ5MDU0MX0.c24V3JQxYlp9L4XgtFqfL6KR31CuTNRC5i-M0t8nMAU");

        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        // when
        UserResponse result = userServiceImpl.findWithRefreshTokenById(1L);

        // then
        assertThat(result.getKakaoId()).isEqualTo(user.getKakaoId());
        assertThat(result.getRefreshToken()).isEqualTo(user.getRefreshToken());
        assertThat(result.getNickName()).isEqualTo(user.getNickName());
        assertThat(result.getProfileImage()).isEqualTo(user.getProfileImage());
        assertThat(result.getThumbnailImage()).isEqualTo(user.getThumbnailImage());
        assertThat(result.getStatus()).isEqualTo(user.getStatus());
        assertThat(result.getRole()).isEqualTo(user.getRole());
    }

    @Test
    @DisplayName("회원 로그아웃")
    void logout(){
        // given
        LogoutMessage logoutMessage = new LogoutMessage();
        logoutMessage.setUserId(1L);

        User user = User.createUser(1L, "황주환", "이미지",
                "이미지", UserRole.USER);
        user.updateRefreshToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiUk9MRSI6IlVTRVIiLCJpYXQiOjE2MjI4ODU3NDEsImV4cCI6MTYyMzQ5MDU0MX0.c24V3JQxYlp9L4XgtFqfL6KR31CuTNRC5i-M0t8nMAU");

        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        // when
        userServiceImpl.logout(logoutMessage);

        // then
        assertThat(user.getRefreshToken()).isNull();;
    }

    @Test
    @DisplayName("회원 프로필 변경 - 이미지 수정")
    void profileUpdate() throws Exception {
        // given
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "프로필사진.png",
                MediaType.IMAGE_PNG_VALUE,
                "<<image>>".getBytes(StandardCharsets.UTF_8));

        UserProfileUpdateRequest userProfileUpdateRequest = new UserProfileUpdateRequest();
        userProfileUpdateRequest.setDeleteImage(false);
        userProfileUpdateRequest.setNickName("황철원");

        User user = User.createUser(1L, "황주환", "이미지", "이미지", UserRole.USER);
        user.changeImage("이미지","이미지","이미지");

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
        UserResponse userResponse = userServiceImpl.profileUpdate(1L,image, userProfileUpdateRequest);

        // then
        assertThat(userResponse.getProfileImage()).isEqualTo("http:이미지");
        assertThat(userResponse.getThumbnailImage()).isEqualTo("http:썸네일이미지");
        assertThat(userResponse.getNickName()).isEqualTo(userProfileUpdateRequest.getNickName());

        then(userRepository).should(times(1)).findById(anyLong());
        then(amazonS3Client).should(times(2)).deleteObject(any(),any());
        then(amazonS3Client).should(times(1)).putObject(any());
        then(amazonS3Client).should(times(2)).getUrl(any(),any());
    }

    @Test
    @DisplayName("회원 프로필 변경 - 이미지 삭제")
    void profileImageDelete(){
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "프로필사진.png",
                MediaType.IMAGE_PNG_VALUE,
                "".getBytes(StandardCharsets.UTF_8));

        // given
        UserProfileUpdateRequest userProfileUpdateRequest = new UserProfileUpdateRequest();
        userProfileUpdateRequest.setDeleteImage(true);

        User user = User.createUser(1L, "황주환", "이미지", "이미지", UserRole.USER);
        user.changeImage("이미지","이미지","이미지");

        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        willDoNothing()
                .given(amazonS3Client)
                .deleteObject(any(),any());

        // when
        UserResponse userResponse = userServiceImpl.profileUpdate(1L,image,userProfileUpdateRequest);

        // then
        assertThat(userResponse.getProfileImage()).isNull();
        assertThat(userResponse.getThumbnailImage()).isNull();
        then(userRepository).should(times(1)).findById(anyLong());
        then(amazonS3Client).should(times(2)).deleteObject(any(),any());
    }

}
