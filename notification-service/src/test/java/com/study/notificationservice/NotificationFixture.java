package com.study.notificationservice;

import com.study.notificationservice.kafka.message.GatheringCreateMessage;
import com.study.notificationservice.model.study.StudyResponse;
import com.study.notificationservice.model.study.StudyUserResponse;

import java.time.LocalDateTime;
import java.util.Arrays;

public class NotificationFixture {

    public static StudyUserResponse TEST_STUDY_USER_RESPONSE =
            new StudyUserResponse(1L, 1L, "fcmToken");
    public static StudyUserResponse TEST_STUDY_USER_RESPONSE1 =
            new StudyUserResponse(2L, 2L, "fcmToken");

    public static StudyResponse TEST_STUDY_RESPONSE =
            new StudyResponse("테스트 스터디",
                    Arrays.asList(TEST_STUDY_USER_RESPONSE,TEST_STUDY_USER_RESPONSE1));

    public static GatheringCreateMessage TEST_GATHERING_CREATE_MESSAGE =
            new GatheringCreateMessage(1L,
                    LocalDateTime.of(2021,7,16,0,0),
                    "온라인","테스트 모임");
}
