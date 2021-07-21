package com.study.studyservice.fixture;

import com.study.studyservice.domain.*;
import com.study.studyservice.model.location.response.LocationResponse;
import com.study.studyservice.model.study.request.StudyCreateRequest;
import com.study.studyservice.model.study.request.StudyFindRequest;
import com.study.studyservice.model.study.request.StudySearchRequest;
import com.study.studyservice.model.study.request.StudyUpdateRequest;
import com.study.studyservice.model.study.response.StudyResponse;
import com.study.studyservice.model.studyuser.StudyUserResponse;
import com.study.studyservice.model.user.UserImage;
import com.study.studyservice.model.user.UserResponse;
import com.study.studyservice.model.waituser.WaitUserResponse;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.study.studyservice.fixture.CategoryFixture.*;
import static com.study.studyservice.fixture.TagFixture.*;

public class StudyFixture {

    public static final Image TEST_IMAGE = Image.createImage(
            "테스트 이미지",
            "테스트 썸네일 이미지",
            "테스트 이미지 저장 이름");

    public static final MockMultipartFile TEST_IMAGE_FILE = new MockMultipartFile(
                "image", "스터디이미지.png",
                MediaType.IMAGE_PNG_VALUE, "<<image>>".getBytes(StandardCharsets.UTF_8));

    public static final MockMultipartFile TEST_FILE = new MockMultipartFile(
            "테스트파일", "테스트 파일.html",
            MediaType.TEXT_HTML_VALUE, "asdf".getBytes(StandardCharsets.UTF_8));

    public static final MockMultipartFile TEST_IMAGE_EMPTY_FILE = new MockMultipartFile(
            "image", "스터디이미지.png",
            MediaType.IMAGE_PNG_VALUE, "".getBytes(StandardCharsets.UTF_8));

    public static final Study TEST_STUDY1 = new Study(1L,
            "테스트 스터디", 5, 1, "테스트 스터디 입니다.",
            true, true, StudyStatus.OPEN, TEST_IMAGE, 1L, TEST_CATEGORY2,
            new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

    public static final Study TEST_STUDY2 = new Study(2L,
            "테스트 스터디2", 5, 1, "테스트 스터디2 입니다.",
            true, true, StudyStatus.OPEN, TEST_IMAGE, 1L, TEST_CATEGORY2,
            new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

    public static final StudyFindRequest TEST_STUDY_FIND_REQUEST = new StudyFindRequest(Arrays.asList(1L,2L));

    public static final StudySearchRequest TEST_STUDY_SEARCH_REQUEST1 = new StudySearchRequest(null,null,false,true);
    public static final StudySearchRequest TEST_STUDY_SEARCH_REQUEST2 = new StudySearchRequest(null,null,true,false);


    public static final StudyTag TEST_STUDY_TAG1 = new StudyTag(1L,TEST_STUDY1,TEST_TAG1);
    public static final StudyTag TEST_STUDY_TAG2 = new StudyTag(2L,TEST_STUDY1,TEST_TAG2);
    public static final StudyUser TEST_STUDY_USER1 = new StudyUser(1L,1L,Role.ADMIN,TEST_STUDY1);
    public static final StudyUser TEST_STUDY_USER2 = new StudyUser(2L,2L,Role.USER,TEST_STUDY1);
    public static final WaitUser TEST_WAIT_USER1 = new WaitUser(1L,3L,TEST_STUDY1);
    public static final WaitUser TEST_WAIT_USER2 = new WaitUser(2L,4L,TEST_STUDY1);


    public static final StudyCreateRequest TEST_STUDY_CREATE_REQUEST1 = new StudyCreateRequest(
                 "테스트 스터디",5,"테스트 스터디 입니다.",Arrays.asList("JPA","스프링"),
            true,true,"1111054000",2L);

    public static final StudyCreateRequest TEST_STUDY_CREATE_REQUEST2 = new StudyCreateRequest(
            "테스트 스터디",5,"테스트 스터디 입니다.",Arrays.asList("JPA","스프링"),
            true,true,null,2L);

    public static final StudyCreateRequest TEST_STUDY_CREATE_REQUEST3 = new StudyCreateRequest(
            "테스트 스터디",5,"테스트 스터디 입니다.",Arrays.asList("JPA","스프링"),
            true,false,null,2L);

    public static final StudyUpdateRequest TEST_STUDY_UPDATE_REQUEST1 = new StudyUpdateRequest(
            "스프링 스터디",0,"스프링 스터디입니다.",2L,Arrays.asList("스프링","JPA"),
            false,false,true,true,"1111054000");

    public static final StudyUpdateRequest TEST_STUDY_UPDATE_REQUEST2 = new StudyUpdateRequest(
            "스프링 스터디",5,"스프링 스터디입니다.",2L,Arrays.asList("스프링","JPA"),
            true,false,true,false,"1111054000");

    public static final StudyUpdateRequest TEST_STUDY_UPDATE_REQUEST3 = new StudyUpdateRequest(
            "스프링 스터디",5,"스프링 스터디입니다.",2L,Arrays.asList("스프링","JPA"),
            false,false,false,true,null);

    public static final StudyUpdateRequest TEST_STUDY_UPDATE_REQUEST4 = new StudyUpdateRequest(
            "스프링 스터디",5,"스프링 스터디입니다.",2L,Arrays.asList("스프링","JPA"),
            false,true,true,true,"1111054000");

    public static final LocationResponse TEST_LOCATION_RESPONSE =
            new LocationResponse(1L,"1111054000","서울특별시","종로구","삼청동",
                    "--리", 37.590758, 126.980996, "H");

    public static final StudyResponse TEST_STUDY_RESPONSE1 = new StudyResponse(1L,"테스트 스터디",5,1,"테스트 스터디 입니다.",
            true,true,StudyStatus.OPEN,TEST_IMAGE,null,TEST_LOCATION_RESPONSE,TEST_CATEGORY_RESPONSE1,TEST_CATEGORY_RESPONSE2,
            Arrays.asList("JPA","스프링"));

    public static final StudyResponse TEST_STUDY_RESPONSE2 = new StudyResponse(1L,"테스트 스터디",5,1,"테스트 스터디 입니다.",
            true,true,StudyStatus.CLOSE,TEST_IMAGE,null,TEST_LOCATION_RESPONSE,TEST_CATEGORY_RESPONSE1,TEST_CATEGORY_RESPONSE2,
            Arrays.asList("JPA","스프링"));

    public static final StudyResponse TEST_STUDY_RESPONSE3 = new StudyResponse(3L,"테스트 스터디3",5,1,"테스트 스터디3 입니다.",
            true,true,StudyStatus.CLOSE,TEST_IMAGE,null, null,null,null,null);

    public static final StudyResponse TEST_STUDY_RESPONSE4 = new StudyResponse(4L,"테스트 스터디4",5,1,"테스트 스터디4 입니다.",
            true,true,StudyStatus.CLOSE,TEST_IMAGE,null,null,null,null,null);

    public static final StudyResponse TEST_STUDY_RESPONSE5 = new StudyResponse(5L,"테스트 스터디5",5,1,"테스트 스터디 5입니다.",
            true,true,StudyStatus.OPEN,TEST_IMAGE,null,null,null,null,Arrays.asList("스프링","MYSQL"));

    public static final StudyResponse TEST_STUDY_RESPONSE6 = new StudyResponse(6L,"테스트 스터디6",5,1,"테스트 스터디 6입니다.",
            true,true,StudyStatus.OPEN,TEST_IMAGE,null,null,null,null,Arrays.asList("뿌직1","뿌직2"));

    public static final StudyResponse TEST_STUDY_RESPONSE7 = new StudyResponse(1L,"테스트 스터디",5,1,"테스트 스터디 입니다.",
            true,true,StudyStatus.OPEN,TEST_IMAGE,false,TEST_LOCATION_RESPONSE,TEST_CATEGORY_RESPONSE1,TEST_CATEGORY_RESPONSE2,
            Arrays.asList("JPA","스프링"));

    public static final UserImage TEST_USER_IMAGE = new UserImage("테스트 썸네일 이미지","테스트 프로필 이미지");

    public static final UserResponse TEST_USER_RESPONSE1 = new UserResponse(2L,"황주환",TEST_USER_IMAGE,"male","20~29",3,800L,"fcmToken");
    public static final UserResponse TEST_USER_RESPONSE2 = new UserResponse(3L,"황철원",TEST_USER_IMAGE,"male","20~29",3,800L,"fcmToken");
    public static final UserResponse TEST_USER_RESPONSE3 = new UserResponse(1L,"한다빈",TEST_USER_IMAGE,"female","20~29",3,800L,"fcmToken");

    public static final WaitUserResponse TEST_WAIT_USER_RESPONSE1
            = new WaitUserResponse(1L,3L,"황철원",TEST_USER_IMAGE,"male","20~29");

    public static final WaitUserResponse TEST_WAIT_USER_RESPONSE2
            = new WaitUserResponse(2L,2L,"황주환",TEST_USER_IMAGE,"male","20~29");

    public static final StudyUserResponse TEST_STUDY_USER_RESPONSE1
            = new StudyUserResponse(1L,1L,"한다빈",TEST_USER_IMAGE,"female","20~29",Role.ADMIN,"fcmToken");

    public static final StudyUserResponse TEST_STUDY_USER_RESPONSE2
            = new StudyUserResponse(2L,2L,"황주환",TEST_USER_IMAGE,"male","20~29",Role.USER,"fcmToken");

    public static Study createTestOnlineStudy(){
        Study study = new Study(1L,
                "테스트 스터디", 5, 1, "테스트 스터디 입니다.",
                true, false, StudyStatus.OPEN, null, null, TEST_CATEGORY2,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        study.getStudyTags().add(new StudyTag(1L,study,TEST_TAG1));
        study.getStudyTags().add(new StudyTag(2L,study,TEST_TAG2));
        study.getStudyUsers().add(new StudyUser(1L,1L,Role.ADMIN,study));
        study.getWaitUsers().add(new WaitUser(1L,2L,study));
        study.getWaitUsers().add(new WaitUser(2L,3L,study));
        return study;
    }

    public static Study createTestOfflineStudy(){
        Study study = new Study(1L,
                "테스트 스터디", 5, 1, "테스트 스터디 입니다.",
                true, true, StudyStatus.OPEN, TEST_IMAGE, 1L, TEST_CATEGORY2,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        study.getStudyTags().add(new StudyTag(1L,study,TEST_TAG1));
        study.getStudyTags().add(new StudyTag(2L,study,TEST_TAG2));
        study.getStudyUsers().add(new StudyUser(1L,1L,Role.ADMIN,study));
        study.getWaitUsers().add(new WaitUser(1L,3L,study));
        study.getWaitUsers().add(new WaitUser(2L,2L,study));
        return study;
    }

    public static Study createTestCloseStudy(){
        Study study = new Study(1L,
                "테스트 스터디", 5, 2, "테스트 스터디 입니다.",
                true, true, StudyStatus.CLOSE, TEST_IMAGE, 1L, TEST_CATEGORY2,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        study.getStudyTags().add(new StudyTag(1L,study,TEST_TAG1));
        study.getStudyTags().add(new StudyTag(2L,study,TEST_TAG2));
        study.getStudyUsers().add(new StudyUser(1L,1L,Role.ADMIN,study));
        study.getStudyUsers().add(new StudyUser(1L,4L,Role.USER,study));
        study.getWaitUsers().add(new WaitUser(1L,3L,study));
        study.getWaitUsers().add(new WaitUser(2L,2L,study));
        return study;
    }







}
