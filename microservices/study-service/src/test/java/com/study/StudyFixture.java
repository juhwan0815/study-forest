package com.study;

import com.study.client.AreaResponse;
import com.study.client.UserResponse;
import com.study.domain.Study;
import com.study.domain.StudyRole;
import com.study.domain.StudyStatus;
import com.study.dto.chatroom.ChatRoomCreateRequest;
import com.study.dto.chatroom.ChatRoomResponse;
import com.study.dto.chatroom.ChatRoomUpdateRequest;
import com.study.dto.study.StudyCreateRequest;
import com.study.dto.study.StudyResponse;
import com.study.dto.study.StudySearchRequest;
import com.study.dto.study.StudyUpdateRequest;
import com.study.dto.studyuser.StudyUserResponse;

import java.util.ArrayList;
import java.util.Arrays;

import static com.study.CategoryFixture.TEST_CATEGORY;
import static com.study.CategoryFixture.TEST_CATEGORY_RESPONSE;
import static com.study.CommonFixture.TEST_SIZE;

public class StudyFixture {

    public static final String TEST_AREA_CODE = "1111054000";
    public static final String TEST_AREA_CITY = "서울특별시";
    public static final String TEST_AREA_GU = "종로구";
    public static final String TEST_AREA_DONG = "삼청동";
    public static final String TEST_AREA_RI = "--리";
    public static final Double TEST_AREA_LET = 37.590758;
    public static final Double TEST_AREA_LEN = 126.980996;
    public static final String TEST_AREA_CODE_TYPE = "H";

    public static final AreaResponse TEST_AREA_RESPONSE
            = new AreaResponse(1L, TEST_AREA_CODE, TEST_AREA_CITY, TEST_AREA_GU, TEST_AREA_DONG, TEST_AREA_RI, TEST_AREA_LET, TEST_AREA_LEN, TEST_AREA_CODE_TYPE);

    public static final String TEST_USER_NICKNAME = "황주환";
    public static final String TEST_USER_AGE_RANGE = "10~19";
    public static final String TEST_USER_GENDER = "male";
    public static final String TEST_USER_IMAGE_URL = "imageUrl";
    public static final String TEST_FCM_TOKEN = "fcmToken";
    public static final Long TEST_USER_AREA_ID = 1L;
    public static final Integer TEST_USER_DISTANCE = 3;

    public static final UserResponse TEST_USER_RESPONSE
            = new UserResponse(1L, TEST_USER_NICKNAME, TEST_USER_AGE_RANGE, TEST_USER_GENDER, TEST_USER_IMAGE_URL, TEST_USER_AREA_ID, TEST_USER_DISTANCE, TEST_FCM_TOKEN);

    public static final String TEST_STUDY_NAME = "스프링";
    public static final Integer TEST_STUDY_NUMBER_OF_PEOPLE = 5;
    public static final Integer TEST_STUDY_CURRENT_OF_PEOPLE = 1;
    public static final String TEST_STUDY_CONTENT = "스프링";
    public static final boolean TEST_STUDY_ONLINE = true;
    public static final boolean TEST_STUDY_OFFLINE = true;
    public static final StudyStatus TEST_STUDY_STATUS = StudyStatus.OPEN;
    public static final String TEST_STUDY_IMAGE_URL = "imageUrl";
    public static final boolean TEST_STUDY_OPEN = true;

    public static final Study TEST_STUDY =
            new Study(1L, TEST_STUDY_NAME, TEST_STUDY_CONTENT, TEST_STUDY_NUMBER_OF_PEOPLE, TEST_STUDY_CURRENT_OF_PEOPLE, TEST_STUDY_ONLINE, TEST_STUDY_ONLINE, TEST_STUDY_STATUS, TEST_USER_IMAGE_URL, TEST_AREA_RESPONSE.getId(), TEST_CATEGORY, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

    public static final StudySearchRequest TEST_STUDY_SEARCH_REQUEST
            = new StudySearchRequest(TEST_STUDY_NAME, TEST_CATEGORY.getId(), TEST_STUDY_ONLINE, TEST_STUDY_OFFLINE, TEST_SIZE, TEST_STUDY.getId());

    public static final StudyCreateRequest TEST_STUDY_CREATE_REQUEST
            = new StudyCreateRequest(TEST_STUDY_NAME, TEST_STUDY_NUMBER_OF_PEOPLE, TEST_STUDY_CONTENT, Arrays.asList("스프링", "JPA"), TEST_STUDY_ONLINE, TEST_STUDY_OFFLINE, TEST_AREA_CODE, TEST_STUDY_IMAGE_URL, TEST_CATEGORY.getId());

    public static final StudyUpdateRequest TEST_STUDY_UPDATE_REQUEST
            = new StudyUpdateRequest(TEST_STUDY_NAME, TEST_STUDY_NUMBER_OF_PEOPLE, TEST_STUDY_CONTENT, Arrays.asList("스프링", "JPA"), TEST_STUDY_ONLINE, TEST_STUDY_OFFLINE, TEST_STUDY_OPEN, TEST_AREA_CODE, TEST_STUDY_IMAGE_URL, TEST_CATEGORY.getId());


    public static final String TEST_CHAT_ROOM_NAME = "공지사항";

    public static final ChatRoomCreateRequest TEST_CHAT_ROOM_CREATE_REQUEST = new ChatRoomCreateRequest(TEST_CHAT_ROOM_NAME);
    public static final ChatRoomUpdateRequest TEST_CHAT_ROOM_UPDATE_REQUEST = new ChatRoomUpdateRequest(TEST_CHAT_ROOM_NAME);

    public static final StudyResponse TEST_STUDY_RESPONSE
            = new StudyResponse(TEST_STUDY.getId(), TEST_STUDY.getName(), TEST_STUDY.getNumberOfPeople(), TEST_STUDY.getCurrentNumberOfPeople(), TEST_STUDY.getContent(), TEST_STUDY.isOnline(), TEST_STUDY.isOffline(), TEST_STUDY.getStatus(),
            TEST_STUDY_IMAGE_URL, TEST_AREA_RESPONSE, TEST_CATEGORY_RESPONSE, TEST_CATEGORY_RESPONSE, Arrays.asList("스프링", "JPA"));

    public static final StudyResponse TEST_STUDY_RESPONSE2
            = new StudyResponse(TEST_STUDY.getId(), TEST_STUDY.getName(), TEST_STUDY.getNumberOfPeople(), TEST_STUDY.getCurrentNumberOfPeople(), null, TEST_STUDY.isOnline(), TEST_STUDY.isOffline(), TEST_STUDY.getStatus(),
            TEST_STUDY_IMAGE_URL, null, null, null, null);

    public static final StudyResponse TEST_STUDY_RESPONSE3
            = new StudyResponse(TEST_STUDY.getId(), TEST_STUDY.getName(), TEST_STUDY.getNumberOfPeople(), TEST_STUDY.getCurrentNumberOfPeople(), null, TEST_STUDY.isOnline(), TEST_STUDY.isOffline(), TEST_STUDY.getStatus(),
            TEST_STUDY_IMAGE_URL, null, null, null, Arrays.asList("스프링", "JPA"));

    public static final StudyUserResponse TEST_STUDY_USER_RESPONSE
            = new StudyUserResponse(TEST_USER_RESPONSE.getUserId(), StudyRole.USER, TEST_USER_RESPONSE.getNickName(), TEST_USER_AGE_RANGE, TEST_USER_GENDER, TEST_USER_IMAGE_URL, TEST_FCM_TOKEN);

    public static final ChatRoomResponse TEST_CHAT_ROOM_RESPONSE
            = new ChatRoomResponse(1L, TEST_CHAT_ROOM_NAME);
}
