package com.study.fixture;

import com.study.client.AreaResponse;
import com.study.client.UserResponse;
import com.study.domain.Image;
import com.study.domain.StudyRole;
import com.study.domain.StudyStatus;
import com.study.domain.WaitStatus;
import com.study.dto.chatroom.ChatRoomCreateRequest;
import com.study.dto.chatroom.ChatRoomResponse;
import com.study.dto.chatroom.ChatRoomUpdateRequest;
import com.study.dto.study.*;
import com.study.dto.studyuser.StudyUserResponse;
import com.study.dto.tag.TagCreateRequest;
import com.study.dto.tag.TagResponse;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.study.fixture.CategoryFixture.TEST_CATEGORY_RESPONSE;

public class StudyFixture {

    public static final Image TEST_IMAGE = Image.createImage("이미지 URL", "이미지 저장 이름");

    public static final MockMultipartFile TEST_IMAGE_FILE = new MockMultipartFile(
            "file", "스터디이미지.png",
            MediaType.IMAGE_PNG_VALUE, "<<image>>".getBytes(StandardCharsets.UTF_8));

    public static final AreaResponse TEST_AREA_RESPONSE
            = new AreaResponse(1L, "1111054000", "서울특별시", "종로구", "삼청동", "--리", 37.590758, 126.980996, "H");

    public static final UserResponse TEST_USER_RESPONSE
            = new UserResponse(1L, "USER", "황주환", "10~19", "male", "이미지 URL", 1L, 3, "fcmToken");

    public static final StudySearchRequest TEST_STUDY_SEARCH_REQUEST
            = new StudySearchRequest("스프링", 1L, true, true);

    public static final StudyCreateRequest TEST_STUDY_CREATE_REQUEST
            = new StudyCreateRequest("스프링", 5, "스프링", Arrays.asList("스프링", "JPA"), true, true, "code", 1L);

    public static final StudyUpdateAreaRequest TEST_STUDY_UPDATE_AREA_REQUEST
            = new StudyUpdateAreaRequest("1111054000");

    public static final StudyUpdateRequest TEST_STUDY_UPDATE_REQUEST
            = new StudyUpdateRequest("리액트", 3, "리액트", false, false, false, 1L);

    public static final ChatRoomCreateRequest TEST_CHAT_ROOM_CREATE_REQUEST = new ChatRoomCreateRequest("공지사항");

    public static final ChatRoomUpdateRequest TEST_CHAT_ROOM_UPDATE_REQUEST = new ChatRoomUpdateRequest("대화방");

    public static final TagCreateRequest TEST_TAG_CREATE_REQUEST = new TagCreateRequest("스프링");

    public static final TagResponse TEST_TAG_RESPONSE = new TagResponse(1L, "스프링");

    public static final StudyResponse TEST_STUDY_RESPONSE
            = new StudyResponse(1L, "스프링", 5, 1, "스프링", true, true, StudyStatus.OPEN, "이미지 Url",
            TEST_AREA_RESPONSE, TEST_CATEGORY_RESPONSE, TEST_CATEGORY_RESPONSE, Arrays.asList(TEST_TAG_RESPONSE), null);

    public static final StudyResponse TEST_STUDY_RESPONSE2
            = new StudyResponse(1L, "스프링", 5, 1, "스프링", true, true, StudyStatus.OPEN, "이미지 Url",
            null, TEST_CATEGORY_RESPONSE, TEST_CATEGORY_RESPONSE, null, null);

    public static final StudyResponse TEST_STUDY_RESPONSE3
            = new StudyResponse(1L, "스프링", 5, 1, "스프링", true, true, StudyStatus.OPEN, "이미지 Url",
            null, null, null, null, null);

    public static final StudyResponse TEST_STUDY_RESPONSE4
            = new StudyResponse(1L, "스프링", 5, 1, "스프링", true, true, StudyStatus.OPEN, "이미지 Url",
            TEST_AREA_RESPONSE, null, null, null, null);

    public static final StudyResponse TEST_STUDY_RESPONSE5
            = new StudyResponse(1L, "스프링", 5, 1, "스프링", true, true, StudyStatus.OPEN, "이미지 Url",
            null, null, null, Arrays.asList(TEST_TAG_RESPONSE), null);

    public static final StudyResponse TEST_STUDY_RESPONSE6
            = new StudyResponse(1L, "스프링", 5, 1, "스프링", true, true, StudyStatus.OPEN, "이미지 Url",
            null, null, null, Arrays.asList(TEST_TAG_RESPONSE), WaitStatus.WAIT);

    public static final StudyUserResponse TEST_STUDY_USER_RESPONSE
            = new StudyUserResponse(1L, StudyRole.USER, "황주환", "10~19", "male", "이미지 URL", "fcmToken");

    public static final ChatRoomResponse TEST_CHAT_ROOM_RESPONSE
            = new ChatRoomResponse(1L, "공지사항");
}
