package com.study;

import com.study.client.UserResponse;
import com.study.domain.Gathering;
import com.study.domain.Place;
import com.study.dto.GatheringCreateRequest;
import com.study.dto.GatheringResponse;
import com.study.dto.GatheringUpdateRequest;

import java.time.LocalDateTime;

public class GatheringFixture {

    public static final GatheringCreateRequest TEST_GATHERING_CREATE_REQUEST
            = new GatheringCreateRequest(LocalDateTime.now(), true, "온라인 스터디", "학교", 37.584009, 126.970626);

    public static final GatheringUpdateRequest TEST_GATHERING_UPDATE_REQUEST
            = new GatheringUpdateRequest(LocalDateTime.now(), true, "온라인 스터디", "학교", 37.584009, 126.970626);

    public static final Gathering TEST_GATHERING = Gathering.createGathering(1L, LocalDateTime.now(), false, "온라인 모임");

    public static final Place TEST_PLACE = new Place("스터디 카페", 37.584009, 126.970626);

    public static final GatheringResponse TEST_GATHERING_RESPONSE
            = new GatheringResponse(1L, 1L, LocalDateTime.now(), 3, true, "온라인 모임입니다~", TEST_PLACE);

    public static final UserResponse TEST_USER_RESPONSE =
            new UserResponse(1L, "USER", "황주환", "10~19", "male", "이미지 URL", 1L, 3, "fcmToken");
}
