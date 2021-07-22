package com.study.notificationservice;

import com.study.notificationservice.kafka.message.*;
import com.study.notificationservice.model.study.StudyResponse;
import com.study.notificationservice.model.study.StudyUserResponse;
import com.study.notificationservice.model.tag.InterestTagResponse;
import com.study.notificationservice.model.tag.TagResponse;
import com.study.notificationservice.model.user.UserResponse;
import com.study.notificationservice.model.user.UserWithTagResponse;

import java.time.LocalDateTime;
import java.util.Arrays;

public class NotificationFixture {

    public static final StudyUserResponse TEST_STUDY_USER_RESPONSE =
            new StudyUserResponse(1L, 1L, "fcmToken");
    public static final StudyUserResponse TEST_STUDY_USER_RESPONSE1 =
            new StudyUserResponse(2L, 2L, "fcmToken");

    public static final StudyResponse TEST_STUDY_RESPONSE =
            new StudyResponse("테스트 스터디",
                    Arrays.asList(TEST_STUDY_USER_RESPONSE,TEST_STUDY_USER_RESPONSE1));

    public static final GatheringCreateMessage TEST_GATHERING_CREATE_MESSAGE =
            new GatheringCreateMessage(1L,
                    LocalDateTime.of(2021,7,16,0,0),
                    "온라인","테스트 모임");

    public static final UserResponse TEST_USER_RESPONSE =
            new UserResponse(1L,"fcmToken");

    public static final StudyApplyFailMessage TEST_STUDY_APPLY_FAIL_MESSAGE =
            new StudyApplyFailMessage(1L,1L,"테스트 스터디");

    public static final StudyApplySuccessMessage TEST_STUDY_APPLY_SUCCESS_MESSAGE =
            new StudyApplySuccessMessage(1L,1L,"테스트 스터디");

    public static final TagResponse TEST_TAG_RESPONSE1 = new TagResponse(1L,"스프링");
    public static final TagResponse TEST_TAG_RESPONSE2 = new TagResponse(2L,"JPA");

    public static final StudyCreateMessage TEST_STUDY_CREATE_MESSAGE =
            new StudyCreateMessage(1L,"테스트 스터디",Arrays.asList(TEST_TAG_RESPONSE1,TEST_TAG_RESPONSE2));

    public static final StudyCreateMessage TEST_STUDY_CREATE_MESSAGE1 =
            new StudyCreateMessage(1L,"테스트 스터디",Arrays.asList(TEST_TAG_RESPONSE1));

    public static final InterestTagResponse TEST_INTEREST_TAG_RESPONSE1 = new InterestTagResponse(1L,1L);
    public static final InterestTagResponse TEST_INTEREST_TAG_RESPONSE2 = new InterestTagResponse(2L,2L);

    public static final UserWithTagResponse TEST_USER_WITH_TAG_RESPONSE1
            = new UserWithTagResponse(1L,"fcmToken",
            Arrays.asList(TEST_INTEREST_TAG_RESPONSE1,TEST_INTEREST_TAG_RESPONSE2));

    public static final UserWithTagResponse TEST_USER_WITH_TAG_RESPONSE2
            = new UserWithTagResponse(2L,"fcmToken",
            Arrays.asList(TEST_INTEREST_TAG_RESPONSE1,TEST_INTEREST_TAG_RESPONSE2));

    public static final ChatCreateMessage TEST_CHAT_CREATE_MESSAGE
            = new ChatCreateMessage(1L,"공지사항","황주환","오늘 스터디있어여",Arrays.asList(1L,2L));

}
