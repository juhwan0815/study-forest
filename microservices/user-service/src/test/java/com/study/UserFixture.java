package com.study;

import com.study.domain.Keyword;
import com.study.domain.User;
import com.study.domain.UserRole;
import com.study.dto.keyword.KeywordCreateRequest;
import com.study.dto.keyword.KeywordResponse;
import com.study.dto.user.UserFindRequest;
import com.study.dto.user.UserResponse;
import com.study.dto.user.UserUpdateDistanceRequest;
import com.study.dto.user.UserUpdateRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class UserFixture {

    public static final String TEST_AUTHORIZATION = "bearer **";
    public static final String TEST_KAKAO_TOKEN = "kakaoToken";

    public static final MockMultipartFile TEST_IMAGE_FILE = new MockMultipartFile(
            "image", "이미지.png",
            MediaType.IMAGE_PNG_VALUE, "<<image>>".getBytes(StandardCharsets.UTF_8));

    public static final MockMultipartFile TEST_EMPTY_IMAGE_FILE = new MockMultipartFile(
            "image", "이미지.png",
            MediaType.IMAGE_PNG_VALUE, "".getBytes(StandardCharsets.UTF_8));

    public static final Long TEST_USER_KAKAO_ID = 1L;
    public static final String TEST_USER_NICKNAME = "황주환";
    public static final String TEST_USER_AGE_RANGE = "10~19";
    public static final String TEST_USER_GENDER = "male";
    public static final String TEST_USER_IMAGE_URL = "imageUrl";
    public static final UserRole TEST_USER_ROLE = UserRole.USER;
    public static final String TEST_FCM_TOKEN = "fcmToken";
    public static final Long TEST_USER_AREA_ID = 1L;
    public static final Integer TEST_USER_DISTANCE = 3;

    public static final String TEST_KEYWORD_CONTENT = "스프링";

    public static final Keyword TEST_KEYWORD = new Keyword(1L, TEST_KEYWORD_CONTENT, null);

    public static final User TEST_USER = new User(1L, TEST_USER_KAKAO_ID, TEST_USER_NICKNAME, TEST_USER_AGE_RANGE, TEST_USER_GENDER,
            TEST_USER_ROLE, TEST_USER_IMAGE_URL, TEST_FCM_TOKEN, TEST_USER_AREA_ID, TEST_USER_DISTANCE, new ArrayList<>());

    public static final UserUpdateDistanceRequest TEST_USER_UPDATE_DISTANCE_REQUEST
            = new UserUpdateDistanceRequest(TEST_USER_DISTANCE);

    public static final UserUpdateRequest TEST_USER_UPDATE_REQUEST
            = new UserUpdateRequest(TEST_USER_NICKNAME, TEST_USER_IMAGE_URL);

    public static final UserFindRequest TEST_USER_FIND_REQUEST
            = new UserFindRequest(Arrays.asList(TEST_USER.getId()));

    public static final KeywordCreateRequest TEST_KEYWORD_CREATE_REQUEST
            = new KeywordCreateRequest(TEST_KEYWORD_CONTENT);

    public static final KeywordResponse TEST_KEYWORD_RESPONSE
            = new KeywordResponse(TEST_KEYWORD.getId(), TEST_KEYWORD_CONTENT);

    public static final UserResponse TEST_USER_RESPONSE
            = new UserResponse(TEST_USER.getId(), TEST_USER_NICKNAME, TEST_USER_AGE_RANGE, TEST_USER_GENDER, TEST_USER_IMAGE_URL, TEST_USER_AREA_ID, TEST_USER_DISTANCE, TEST_FCM_TOKEN);

}
