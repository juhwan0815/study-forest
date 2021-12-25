package com.study;

import com.study.domain.Image;
import com.study.domain.Keyword;
import com.study.domain.User;
import com.study.domain.UserRole;
import com.study.dto.keyword.KeywordCreateRequest;
import com.study.dto.keyword.KeywordResponse;
import com.study.dto.user.UserFindRequest;
import com.study.dto.user.UserResponse;
import com.study.dto.user.UserUpdateDistanceRequest;
import com.study.dto.user.UserUpdateNickNameRequest;

import java.util.Arrays;

public class UserFixture {

    public static final Image TEST_IMAGE = Image.createImage("이미지 URL", "이미지 저장 이름");

    public static final User TEST_USER = User.createUser(1L, "황주환", "10~19", "male", UserRole.USER);

    public static final UserUpdateDistanceRequest TEST_USER_UPDATE_DISTANCE_REQUEST = new UserUpdateDistanceRequest(6);

    public static final UserUpdateNickNameRequest TEST_USER_UPDATE_NICKNAME_REQUEST= new UserUpdateNickNameRequest("황철원");

    public static final UserFindRequest TEST_USER_FIND_REQUEST = new UserFindRequest(Arrays.asList(1L));

    public static final KeywordCreateRequest TEST_KEYWORD_CREATE_REQUEST = new KeywordCreateRequest("스프링");

    public static final Keyword TEST_KEYWORD = new Keyword(1L, "스프링", TEST_USER);

    public static final KeywordResponse TEST_KEYWORD_RESPONSE = new KeywordResponse(1L, "스프링");

    public static final UserResponse TEST_USER_RESPONSE1
            = new UserResponse(1L, UserRole.USER, "황주환", "10~19", "male", "이미지 URL", null, 5,null);

    public static final UserResponse TEST_USER_RESPONSE2
            = new UserResponse(1L, UserRole.USER, "황주환", "10~19", "male", "이미지 URL", 1L, 5,"fcmToken");
}
