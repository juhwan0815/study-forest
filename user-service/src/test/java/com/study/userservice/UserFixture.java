package com.study.userservice;

import com.study.userservice.domain.Image;
import com.study.userservice.domain.User;
import com.study.userservice.domain.UserRole;
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
                    "이미지","10~19","male");

    public static final Image TEST_Image
            = Image.createImage("썸네일 이미지","이미지","이미지 저장");

    public static final User TEST_USER =
            new User(1L,1L,null,"황주환","10~19","male",0,
                    TEST_Image, UserRole.USER,null,null);

    public static final User TEST_USER2 =
            new User(2L,1L,2L,"황철원","10~19","male",0,
                    TEST_Image, UserRole.USER,null,null);

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
            = new UserResponse(1L,1L,"황주환",TEST_Image,"male","10~19",2,1L);

    public static final UserResponse TEST_USER_RESPONSE2
            = new UserResponse(2L,2L,"황철원",TEST_Image,"male","10~19",3,2L);

    public static final UserFindRequest TEST_USER_FIND_REQUEST = new UserFindRequest(Arrays.asList(1L,2L));

    public static User createTestUser(){
        return new User(1L,1L,1L,"황주환","10~19","male",2,
                TEST_Image, UserRole.USER,new ArrayList<>(),new ArrayList<>());
    }


}
