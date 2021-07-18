package com.study.gatheringservice;

import com.study.gatheringservice.domain.Gathering;
import com.study.gatheringservice.domain.GatheringUser;
import com.study.gatheringservice.domain.Place;
import com.study.gatheringservice.domain.Shape;
import com.study.gatheringservice.model.gathering.GatheringCreateRequest;
import com.study.gatheringservice.model.gathering.GatheringResponse;
import com.study.gatheringservice.model.gathering.GatheringUpdateRequest;
import com.study.gatheringservice.model.gatheringuser.GatheringUserResponse;
import com.study.gatheringservice.model.studyuser.Role;
import com.study.gatheringservice.model.studyuser.StudyUserResponse;
import com.study.gatheringservice.model.user.UserImage;
import com.study.gatheringservice.model.user.UserResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class GatheringFixture {

    public static LocalDateTime TEST_LOCAL_DATE_TIME1 = LocalDateTime.of(2021,7,16,17,00);
    public static LocalDateTime TEST_LOCAL_DATE_TIME2 = LocalDateTime.of(2021,7,16,18,00);

    public static Place TEST_PLACE = new Place("테스트 장소",37.584009,126.970626);

    public static UserImage TEST_USER_IMAGE = new UserImage("테스트 썸네일 이미지","테스트 이미지");

    public static UserResponse TEST_USER_RESPONSE1
            = new UserResponse(1L,"황주환",TEST_USER_IMAGE,"male","20~29",null,null);

    public static StudyUserResponse TEST_STUDY_USER_RESPONSE1
            = new StudyUserResponse(1L,1L,"황주환", Role.ADMIN);

    public static StudyUserResponse TEST_STUDY_USER_RESPONSE2
            = new StudyUserResponse(2L,2L,"황철원", Role.USER);

    public static GatheringUserResponse TEST_GATHERING_USER_RESPONSE1
            = new GatheringUserResponse(1L,1L,"황주환",TEST_USER_IMAGE,"male","20~29",true);

    public static GatheringUserResponse TEST_GATHERING_USER_RESPONSE2
            = new GatheringUserResponse(2L,2L,"황철원",TEST_USER_IMAGE,"male","20~29",false);

    public static GatheringCreateRequest TEST_GATHERING_CREATE_REQUEST1
            = new GatheringCreateRequest(TEST_LOCAL_DATE_TIME1, Shape.ONLINE,"테스트 모임",null,null,null);

    public static GatheringCreateRequest TEST_GATHERING_CREATE_REQUEST2
            = new GatheringCreateRequest(TEST_LOCAL_DATE_TIME1, Shape.OFFLINE,"테스트 모임","테스트 장소",37.584009,126.970626);

    public static GatheringUpdateRequest TEST_GATHERING_UPDATE_REQUEST1
            = new GatheringUpdateRequest(TEST_LOCAL_DATE_TIME2, Shape.ONLINE,"테스트 모임",null,null,null);

    public static GatheringUpdateRequest TEST_GATHERING_UPDATE_REQUEST2
            = new GatheringUpdateRequest(TEST_LOCAL_DATE_TIME2, Shape.OFFLINE,"테스트 모임","테스트 장소",37.584009,126.970626);

    public static Gathering TEST_GATHERING1
            = new Gathering(1L,1L,TEST_LOCAL_DATE_TIME1,Shape.ONLINE,"테스트 모임",1,null,null);

    public static Gathering TEST_GATHERING2
            = new Gathering(2L,1L,TEST_LOCAL_DATE_TIME2,Shape.OFFLINE,"테스트 모임",1,TEST_PLACE,null);


    public static GatheringResponse TEST_GATHERING_RESPONSE1
            = new GatheringResponse(1L,1L,TEST_LOCAL_DATE_TIME1,1,Shape.ONLINE,"테스트 모임",null,null);

    public static GatheringResponse TEST_GATHERING_RESPONSE2
            = new GatheringResponse(2L,1L,TEST_LOCAL_DATE_TIME1,1,Shape.OFFLINE,"테스트 모임",TEST_PLACE,null);

    public static GatheringResponse TEST_GATHERING_RESPONSE3
            = new GatheringResponse(1L,1L,TEST_LOCAL_DATE_TIME2,1,Shape.OFFLINE,"테스트 모임",TEST_PLACE,null);

    public static GatheringResponse TEST_GATHERING_RESPONSE4
            = new GatheringResponse(1L,1L,TEST_LOCAL_DATE_TIME2,1,Shape.OFFLINE,"테스트 모임",TEST_PLACE,false);


    public static Gathering createOnlineGathering(){
        Gathering gathering = new Gathering(1L, 1L, TEST_LOCAL_DATE_TIME1, Shape.ONLINE, "테스트 모임",
                                1, null, new ArrayList<>());
        gathering.getGatheringUsers().add(new GatheringUser(1L,1L,true,gathering));
        return gathering;
    }

    public static Gathering createOfflineGathering(){
        Place place = Place.createPlace("테스트 장소", 37.584009, 126.970626);
        Gathering gathering = new Gathering(1L, 1L, TEST_LOCAL_DATE_TIME1, Shape.OFFLINE, "테스트 모임",
                1, place, new ArrayList<>());
        gathering.getGatheringUsers().add(new GatheringUser(1L,1L,true,gathering));
        return gathering;
    }

}