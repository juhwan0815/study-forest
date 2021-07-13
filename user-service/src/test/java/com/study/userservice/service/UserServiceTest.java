package com.study.userservice.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.study.userservice.client.StudyServiceClient;
import com.study.userservice.domain.StudyApplyStatus;
import com.study.userservice.domain.User;
import com.study.userservice.exception.UserException;
import com.study.userservice.kafka.sender.UserDeleteMessageSender;
import com.study.userservice.model.interestTag.InterestTagResponse;
import com.study.userservice.model.studyapply.StudyApplyResponse;
import com.study.userservice.model.user.UserResponse;
import com.study.userservice.repository.UserRepository;
import com.study.userservice.repository.query.UserQueryRepository;
import com.study.userservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.study.userservice.UserFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private UserQueryRepository userQueryRepository;

    @Mock
    private AmazonS3Client amazonS3Client;

    @Mock
    private StudyServiceClient studyServiceClient;

    @Mock
    private UserDeleteMessageSender userDeleteMessageSender;


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
        assertThat(userResponse.getLocationId()).isNull();
        assertThat(userResponse.getGender()).isEqualTo(TEST_USER_LOGIN_REQUEST.getGender());
        assertThat(userResponse.getAgeRange()).isEqualTo(TEST_USER_LOGIN_REQUEST.getAgeRange());
        assertThat(userResponse.getSearchDistance()).isEqualTo(3);
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
        assertThat(userResponse.getSearchDistance()).isEqualTo(3);

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
        UserResponse userResponse = userService.updateProfile(1L,null,
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

    @Test
    @DisplayName("회원 ID로 회원을 조회한다.")
    void findById(){
        // given
        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(TEST_USER));

        // when
        UserResponse result = userService.findById(1L);

        // then
        assertThat(result.getId()).isEqualTo(TEST_USER.getId());
        assertThat(result.getKakaoId()).isEqualTo(TEST_USER.getId());
        assertThat(result.getNickName()).isEqualTo(TEST_USER.getNickName());
        assertThat(result.getGender()).isEqualTo(TEST_USER.getGender());
        assertThat(result.getAgeRange()).isEqualTo(TEST_USER.getAgeRange());
        assertThat(result.getImage()).isEqualTo(TEST_USER.getImage());
        assertThat(result.getLocationId()).isNull();
        assertThat(result.getNumberOfStudyApply()).isEqualTo(TEST_USER.getNumberOfStudyApply());
        assertThat(result.getSearchDistance()).isEqualTo(3);

        then(userRepository).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("회원 ID로 회원 탈퇴를 한다.")
    void delete(){
        // given
        given(userRepository.findById(anyLong()))
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
        then(userRepository).should(times(1)).findById(anyLong());
        then(userRepository).should(times(1)).delete(any());
        then(userDeleteMessageSender).should(times(1)).send(any());
    }

    @Test
    @DisplayName("회원 ID 리스트로 회원 목록을 조회한다.")
    void findByIdIn(){
        // given
        List<User> userList = Arrays.asList(TEST_USER, TEST_USER2);
        given(userQueryRepository.findByIdIn(any()))
                .willReturn(userList);

        // when
        List<UserResponse> result = userService.findByIdIn(TEST_USER_FIND_REQUEST);

        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getId()).isEqualTo(TEST_USER.getId());
        assertThat(result.get(1).getId()).isEqualTo(TEST_USER2.getId());
        then(userQueryRepository).should(times(1)).findByIdIn(any());
    }

    @Test
    @DisplayName("회원의 지역 정보를 변경한다.")
    void changeLocation(){
        // given
        User user = createTestUser();
        given(userRepository.findById(any()))
                .willReturn(Optional.of(user));

        // when
        UserResponse result = userService.updateLocation(1L, 2L);

        // then
        assertThat(result.getLocationId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("회원의 관심 태그를 추가한다.")
    void addInterestTag(){
        // given
        User user = createTestUser();
        given(userQueryRepository.findWithInterestTagById(any()))
                .willReturn(user);

        // when
        userService.addInterestTag(1L,1L);

        // then
        assertThat(user.getInterestTags().size()).isEqualTo(1);
        assertThat(user.getInterestTags().get(0).getTagId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("예외 테스트 : 이미 존재하는 관심태그를 중복으로 추가하면 예외가 발생한다.")
    void addDuplicatedInterestTag(){
        // given
        User user = createTestUser();
        user.addInterestTag(1L);

        given(userQueryRepository.findWithInterestTagById(any()))
                .willReturn(user);

        // when
        assertThrows(UserException.class, ()-> userService.addInterestTag(1L,1L));
    }

    @Test
    @DisplayName("회원의 관심태그를 삭제한다.")
    void deleteInterestTag(){
        // given
        User user = createTestUser();
        user.addInterestTag(1L);

        given(userQueryRepository.findWithInterestTagById(any()))
                .willReturn(user);

        // when
        userService.deleteInterestTag(1L,1L);

        // then
        assertThat(user.getInterestTags().size()).isEqualTo(0);
        then(userQueryRepository).should(times(1)).findWithInterestTagById(any());
    }

    @Test
    @DisplayName("예외테스트 : 회원의 관심 태그 목록에 존재하지 않는 태그를 삭제할 경우 예외가 발생한다.")
    void deleteInterestTagWhenNotExist(){
        // given
        User user = createTestUser();
        given(userQueryRepository.findWithInterestTagById(any()))
                .willReturn(user);

        // when
        assertThrows(UserException.class,()->userService.deleteInterestTag(1L,1L));
    }

    @Test
    @DisplayName("회원의 관심 태그 목록을 조회한다.")
    void findInterestTagsByUserId(){
        // given
        User user = createTestUser2();
        given(userQueryRepository.findWithInterestTagById(any()))
                .willReturn(user);

        given(studyServiceClient.findTagsByIdIn(any()))
                .willReturn(Arrays.asList(TEST_TAG_RESPONSE1,TEST_TAG_RESPONSE2));

        // when
        List<InterestTagResponse> result = userService.findInterestTagByUserId(1L);

        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getTagId()).isEqualTo(TEST_TAG_RESPONSE1.getId());
        assertThat(result.get(1).getTagId()).isEqualTo(TEST_TAG_RESPONSE2.getId());

        then(userQueryRepository).should(times(1)).findWithInterestTagById(any());
        then(studyServiceClient).should(times(1)).findTagsByIdIn(any());
    }

    @Test
    @DisplayName("회원의 스터디 참가 신청 이력을 추가한다.")
    void createStudyApply(){
        // given
        User user = createTestUser();
        given(userRepository.findById(any()))
                .willReturn(Optional.of(user));

        // when
        userService.createStudyApply(TEST_STUDY_APPLY_CREATE_MESSAGE);

        // then
        assertThat(user.getNumberOfStudyApply()).isEqualTo(1);
        assertThat(user.getStudyApplies().size()).isEqualTo(1);
        assertThat(user.getStudyApplies().get(0).getStudyId()).isEqualTo(TEST_STUDY_APPLY_CREATE_MESSAGE.getStudyId());
        assertThat(user.getStudyApplies().get(0).getStatus()).isEqualTo(StudyApplyStatus.WAIT);

        then(userRepository).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("회원의 스터디 참가 신청 이력을 실패로 변경한다.")
    void failStudyApply(){
        // given
        User user = createTestUser2();
        given(userQueryRepository.findWithStudyApplyById(any()))
                .willReturn(user);

        // when
        userService.FailStudyApply(TEST_STUDY_APPLY_FAIL_MESSAGE);

        // then
        assertThat(user.getStudyApplies().size()).isEqualTo(2);
        assertThat(user.getNumberOfStudyApply()).isEqualTo(2);
        assertThat(user.getStudyApplies().get(0).getStatus()).isEqualTo(StudyApplyStatus.FAIL);
        then(userQueryRepository).should(times(1)).findWithStudyApplyById(any());
    }

    @Test
    @DisplayName("회원의 스터디 참가 신청 이력을 실패로 변경한다.")
    void SuccessStudyApply(){
        // given
        User user = createTestUser2();
        given(userQueryRepository.findWithStudyApplyById(any()))
                .willReturn(user);

        // when
        userService.SuccessStudyApply(TEST_STUDY_APPLY_SUCCESS_MESSAGE);

        // then
        assertThat(user.getStudyApplies().size()).isEqualTo(2);
        assertThat(user.getNumberOfStudyApply()).isEqualTo(2);
        assertThat(user.getStudyApplies().get(0).getStatus()).isEqualTo(StudyApplyStatus.SUCCESS);
        then(userQueryRepository).should(times(1)).findWithStudyApplyById(any());
    }

    @Test
    @DisplayName("회원의 스터디 참가 신청 이력을 조회한다.")
    void findStudyAppliesByUserId(){
        // given
        User user = createTestUser2();
        given(userQueryRepository.findWithStudyApplyById(any()))
                .willReturn(user);

        given(studyServiceClient.findStudiesByIdIn(any()))
                .willReturn(Arrays.asList(TEST_STUDY_RESPONSE1));

        // when
        List<StudyApplyResponse> result = userService.findStudyAppliesByUserId(1L);

        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(user.getStudyApplies().size()).isEqualTo(1);
        then(userQueryRepository).should(times(1)).findWithStudyApplyById(any());
        then(studyServiceClient).should(times(1)).findStudiesByIdIn(any());
    }

    @Test
    @DisplayName("회원의 오프라인 검색 거리를 변경한다.")
    void updateSearchDistance(){
        // given
        User user = createTestUser();

        given(userRepository.findById(any()))
                .willReturn(Optional.of(user));

        // when
        UserResponse result = userService.updateSearchDistance(1L, 3);

        // then
        assertThat(result.getSearchDistance()).isEqualTo(3);
        then(userRepository).should(times(1)).findById(any());
    }

}
