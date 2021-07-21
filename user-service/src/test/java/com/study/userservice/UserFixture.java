package com.study.userservice;

import com.study.userservice.domain.*;
import com.study.userservice.kafka.message.*;
import com.study.userservice.model.interestTag.InterestTagResponse;
import com.study.userservice.model.study.StudyResponse;
import com.study.userservice.model.studyapply.StudyApplyResponse;
import com.study.userservice.model.tag.TagResponse;
import com.study.userservice.model.user.UserFindRequest;
import com.study.userservice.model.user.UserLoginRequest;
import com.study.userservice.model.user.UserUpdateProfileRequest;
import com.study.userservice.model.user.UserResponse;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class UserFixture {

    public static final UserLoginRequest TEST_USER_LOGIN_REQUEST =
            new UserLoginRequest(1L,"황주환","썸네일 이미지",
                    "이미지","10~19","male","fcmToken");

    public static final Image TEST_Image
            = Image.createImage("썸네일 이미지","이미지","이미지 저장");

    public static final User TEST_USER =
            new User(1L,1L,null,3,"황주환","10~19","male",0,
                    "fcmToken",TEST_Image, UserRole.USER,null,null);

    public static final User TEST_USER2 =
            new User(2L,1L,2L,3,"황철원","10~19","male",0,
                    "fcmToken",TEST_Image, UserRole.USER,null,null);

    public static final MockMultipartFile TEST_IMAGE_FILE = new MockMultipartFile(
            "image",
            "프로필사진.png",
            MediaType.IMAGE_PNG_VALUE,
            "<<image>>".getBytes(StandardCharsets.UTF_8));

    public static final MockMultipartFile TEST_EMPTY_IMAGE_FILE = new MockMultipartFile(
            "image",
            "프로필사진.png",
            MediaType.IMAGE_PNG_VALUE,
            "".getBytes(StandardCharsets.UTF_8));

    public static final UserUpdateProfileRequest TEST_USER_PROFILE_UPDATE_REQUEST1
            = new UserUpdateProfileRequest(false,"황철원");

    public static final UserUpdateProfileRequest TEST_USER_PROFILE_UPDATE_REQUEST2
            = new UserUpdateProfileRequest(true,"황철원");

    public static final UserResponse TEST_USER_RESPONSE
            = new UserResponse(1L,1L,"황주환",TEST_Image,"male","10~19",2,1L,3,UserRole.USER,null);

    public static final UserResponse TEST_USER_RESPONSE2
            = new UserResponse(2L,2L,"황철원",TEST_Image,"male","10~19",3,2L,3,UserRole.USER,null);

    public static final UserResponse TEST_USER_RESPONSE3
            = new UserResponse(1L,1L,"황주환",TEST_Image,"male","10~19",3,1L,3,UserRole.USER,"FCM Token");

    public static final UserResponse TEST_USER_RESPONSE4
            = new UserResponse(2L,2L,"황철원",TEST_Image,"male","10~19",3,2L,3,UserRole.USER,"FCM Token");


    public static final UserFindRequest TEST_USER_FIND_REQUEST = new UserFindRequest(Arrays.asList(1L,2L));

    public static final TagResponse TEST_TAG_RESPONSE1 = new TagResponse(1L,"스프링");
    public static final TagResponse TEST_TAG_RESPONSE2 = new TagResponse(2L,"스프링");

    public static final InterestTagResponse TEST_INTEREST_TAG_RESPONSE1 = new InterestTagResponse(1L,1L,"스프링");
    public static final InterestTagResponse TEST_INTEREST_TAG_RESPONSE2 = new InterestTagResponse(1L,1L,"스프링");

    public static final StudyApplyCreateMessage TEST_STUDY_APPLY_CREATE_MESSAGE =
            new StudyApplyCreateMessage(1L,1L);

    public static final StudyApplySuccessMessage TEST_STUDY_APPLY_SUCCESS_MESSAGE =
            new StudyApplySuccessMessage(1L,1L);

    public static final StudyApplyFailMessage TEST_STUDY_APPLY_FAIL_MESSAGE =
            new StudyApplyFailMessage(1L,1L);

    public static final StudyApplyCancelMessage TEST_STUDY_APPLY_CANCEL_MESSAGE =
            new StudyApplyCancelMessage(1L,1L);

    public static final StudyDeleteMessage TEST_STUDY_DELETE_MESSAGE =
            new StudyDeleteMessage(1L);

    public static final StudyResponse TEST_STUDY_RESPONSE1 = new StudyResponse(1L,"테스트 스터디1");
    public static final StudyResponse TEST_STUDY_RESPONSE2 = new StudyResponse(2L,"테스트 스터디2");

    public static final StudyApplyResponse TEST_STUDY_APPLY_RESPONSE1
            = new StudyApplyResponse(1L,1L,"테스트 스터디1",StudyApplyStatus.FAIL);
    public static final StudyApplyResponse TEST_STUDY_APPLY_RESPONSE2
            = new StudyApplyResponse(2L,1L,"테스트 스터디1",StudyApplyStatus.SUCCESS);
    public static final StudyApplyResponse TEST_STUDY_APPLY_RESPONSE3
            = new StudyApplyResponse(3L,2L,"테스트 스터디2",StudyApplyStatus.WAIT);

    public static User createTestUser(){
        return new User(1L,1L,1L,3,"황주환","10~19","male",0,
                "fcmToken",TEST_Image, UserRole.USER,new ArrayList<>(),new ArrayList<>());
    }

    public static User createTestUser2(){
        User user = new User(1L, 1L, 1L, 3,"황주환", "10~19", "male", 2,
                "fcmToken",TEST_Image, UserRole.USER, new ArrayList<>(), new ArrayList<>());
        user.getInterestTags().add(new InterestTag(1L,1L,user));
        user.getInterestTags().add(new InterestTag(2L,2L,user));
        user.getStudyApplies().add(new StudyApply(2L,1L,StudyApplyStatus.WAIT,user));
        user.getStudyApplies().add(new StudyApply(1L,1L,StudyApplyStatus.FAIL,user));

        return user;
    }
}
