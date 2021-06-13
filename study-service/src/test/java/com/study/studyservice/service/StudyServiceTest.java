package com.study.studyservice.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.study.studyservice.client.LocationServiceClient;
import com.study.studyservice.domain.*;
import com.study.studyservice.exception.StudyException;
import com.study.studyservice.kafka.sender.KafkaStudyDeleteMessageSender;
import com.study.studyservice.kafka.sender.KafkaStudyJoinMessageSender;
import com.study.studyservice.model.location.response.LocationResponse;
import com.study.studyservice.model.study.request.StudyCreateRequest;
import com.study.studyservice.model.study.request.StudyUpdateRequest;
import com.study.studyservice.model.study.response.StudyResponse;
import com.study.studyservice.repository.CategoryRepository;
import com.study.studyservice.repository.StudyRepository;
import com.study.studyservice.repository.StudyUserRepository;
import com.study.studyservice.repository.TagRepository;
import com.study.studyservice.repository.query.StudyQueryRepository;
import com.study.studyservice.service.impl.StudyServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class StudyServiceTest {

    @InjectMocks
    private StudyServiceImpl studyService;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private StudyRepository studyRepository;

    @Mock
    private LocationServiceClient locationServiceClient;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private StudyQueryRepository studyQueryRepository;

    @Mock
    private StudyUserRepository studyUserRepository;

    @Mock
    private AmazonS3Client amazonS3Client;

    @Mock
    private KafkaStudyDeleteMessageSender kafkaStudyDeleteMessageSender;

    @Mock
    private KafkaStudyJoinMessageSender kafkaStudyJoinMessageSender;

    @Test
    @DisplayName("스터디 생성 - 오류")
    void createStudyNotWithLocationCode(){
        // given
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "스터디이미지.png",
                MediaType.IMAGE_PNG_VALUE,
                "<<image>>".getBytes(StandardCharsets.UTF_8));

        StudyCreateRequest studyCreateRequest = new StudyCreateRequest();
        studyCreateRequest.setName("스프링 스터디");
        studyCreateRequest.setLocationCode(null);
        studyCreateRequest.setCategoryId(1L);
        studyCreateRequest.setContent("안녕하세요");
        studyCreateRequest.setOnline(true);
        studyCreateRequest.setOffline(true);
        studyCreateRequest.setTags(Arrays.asList("JPA","스프링"));
        studyCreateRequest.setNumberOfPeople(5);

        Category parentCategory = Category.createCategory("개발", null);
        Category childCategory = Category.createCategory("백엔드", parentCategory);

        given(categoryRepository.findWithParentById(any()))
                .willReturn(Optional.of(childCategory));

        // when
        assertThrows(StudyException.class,()->studyService.create(1L,image,studyCreateRequest));
    }

    @Test
    @DisplayName("스터디 생성 - 이미지 X,온라인만")
    void createStudyNotWithImageAndLocation() throws MalformedURLException {
        // given
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "스터디이미지.png",
                MediaType.IMAGE_PNG_VALUE,
                "".getBytes(StandardCharsets.UTF_8));

        StudyCreateRequest studyCreateRequest = new StudyCreateRequest();
        studyCreateRequest.setName("스프링 스터디");
        studyCreateRequest.setLocationCode("1111051500");
        studyCreateRequest.setCategoryId(1L);
        studyCreateRequest.setContent("안녕하세요 스프링 스터디입니다.");
        studyCreateRequest.setOnline(true);
        studyCreateRequest.setOffline(false);
        studyCreateRequest.setTags(Arrays.asList("JPA","스프링"));
        studyCreateRequest.setNumberOfPeople(5);

        Category parentCategory = Category.createCategory("개발", null);
        Category childCategory = Category.createCategory("백엔드", parentCategory);

        Tag tag1 = Tag.createTag("스프링");
        Tag tag2 = Tag.createTag("JPA");
        List<Tag> tags = new ArrayList<>();
        tags.add(tag1);

        StudyUser studyUser = StudyUser.createStudyUser(1L, Role.ADMIN);

        List<StudyTag> studyTagList = new ArrayList<>();
        StudyTag studyTag1 = StudyTag.createStudyTag(tag1);
        StudyTag studyTag2 = StudyTag.createStudyTag(tag2);
        studyTagList.add(studyTag1);
        studyTagList.add(studyTag2);

        Study study = Study.createStudy("스프링 스터디",
                5, "안녕하세요 스프링 스터디입니다.",
                true, true, null,
                null, null, null, childCategory, studyUser, studyTagList);


        given(categoryRepository.findWithParentById(any()))
                .willReturn(Optional.of(childCategory));

        given(tagRepository.findByNameIn(any()))
                .willReturn(tags);

        given(tagRepository.save(any()))
                .willReturn(tag2);

        given(studyRepository.save(any()))
                .willReturn(study);

        // when
        StudyResponse result = studyService.create(1L, image,studyCreateRequest);

        // then
        assertThat(result.getName()).isEqualTo(studyCreateRequest.getName());
        assertThat(result.getContent()).isEqualTo(studyCreateRequest.getContent());
        assertThat(result.getStudyThumbnailImage()).isNull();;
        assertThat(result.getStudyImage()).isNull();
        assertThat(result.getStatus()).isEqualTo(StudyStatus.OPEN);
        assertThat(result.getLocation().getId()).isEqualTo(null);
        assertThat(result.getParentCategory().getName()).isEqualTo(parentCategory.getName());
        assertThat(result.getChildCategory().getName()).isEqualTo(childCategory.getName());
        assertThat(result.getStudyTags().size()).isEqualTo(2);
        assertThat(result.getNumberOfPeople()).isEqualTo(studyCreateRequest.getNumberOfPeople());
        assertThat(result.getCurrentNumberOfPeople()).isEqualTo(1);
        assertThat(result.isOffline()).isEqualTo(true);
        assertThat(result.isOnline()).isEqualTo(true);

        then(categoryRepository).should(times(1)).findWithParentById(any());
        then(tagRepository).should(times(1)).findByNameIn(any());
        then(tagRepository).should(times(1)).save(any());
        then(studyRepository).should(times(1)).save(any());
    }

    @Test
    @DisplayName("스터디 생성 - 이미지 O,오프라인까지")
    void createStudyWithImageAndLocation() throws MalformedURLException {
        // given
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "스터디이미지.png",
                "image/png",
                "<<image>>".getBytes(StandardCharsets.UTF_8));

        StudyCreateRequest studyCreateRequest = new StudyCreateRequest();
        studyCreateRequest.setName("스프링 스터디");
        studyCreateRequest.setLocationCode("1111051500");
        studyCreateRequest.setCategoryId(1L);
        studyCreateRequest.setContent("안녕하세요 스프링 스터디입니다.");
        studyCreateRequest.setOnline(true);
        studyCreateRequest.setOffline(true);
        studyCreateRequest.setTags(Arrays.asList("JPA","스프링"));
        studyCreateRequest.setNumberOfPeople(5);

        Category parentCategory = Category.createCategory("개발", null);
        Category childCategory = Category.createCategory("백엔드", parentCategory);

        LocationResponse locationResponse = new LocationResponse();
        locationResponse.setId(1L);
        locationResponse.setCity("서울특별시");
        locationResponse.setCode("1111051500");
        locationResponse.setGu("종로구");
        locationResponse.setDong("삼청동");
        locationResponse.setRi("--리");
        locationResponse.setLen(126.980996);
        locationResponse.setLet(37.590758);
        locationResponse.setCodeType("H");

        Tag tag1 = Tag.createTag("스프링");
        Tag tag2 = Tag.createTag("JPA");
        List<Tag> tags = new ArrayList<>();
        tags.add(tag1);

        StudyUser studyUser = StudyUser.createStudyUser(1L, Role.ADMIN);

        List<StudyTag> studyTagList = new ArrayList<>();
        StudyTag studyTag1 = StudyTag.createStudyTag(tag1);
        StudyTag studyTag2 = StudyTag.createStudyTag(tag2);
        studyTagList.add(studyTag1);
        studyTagList.add(studyTag2);

        Study study = Study.createStudy("스프링 스터디",
                5, "안녕하세요 스프링 스터디입니다.",
                true, true, "저장 이름",
                "http:이미지", "http:썸네일이미지", 1L, childCategory, studyUser, studyTagList);

        given(categoryRepository.findWithParentById(any()))
                .willReturn(Optional.of(childCategory));


        given(locationServiceClient.findLocationByCode(any()))
                .willReturn(locationResponse);

        given(amazonS3Client.putObject(any()))
                .willReturn(null);

        given(amazonS3Client.getUrl(any(),any()))
                .willReturn(new URL("http:이미지"))
                .willReturn(new URL("http:썸네일이미지"));

        given(tagRepository.findByNameIn(any()))
                .willReturn(tags);

        given(tagRepository.save(any()))
                .willReturn(tag2);

        given(studyRepository.save(any()))
                .willReturn(study);

        // when
        StudyResponse result = studyService.create(1L,image,studyCreateRequest);

        // then
        assertThat(result.getName()).isEqualTo(studyCreateRequest.getName());
        assertThat(result.getContent()).isEqualTo(studyCreateRequest.getContent());
        assertThat(result.getStudyThumbnailImage()).isEqualTo("http:썸네일이미지");
        assertThat(result.getStudyImage()).isEqualTo("http:이미지");
        assertThat(result.getStatus()).isEqualTo(StudyStatus.OPEN);
        assertThat(result.getLocation().getId()).isNotNull();
        assertThat(result.getLocation().getCode()).isEqualTo(studyCreateRequest.getLocationCode());
        assertThat(result.getParentCategory().getName()).isEqualTo(parentCategory.getName());
        assertThat(result.getChildCategory().getName()).isEqualTo(childCategory.getName());
        assertThat(result.getStudyTags().size()).isEqualTo(2);
        assertThat(result.getNumberOfPeople()).isEqualTo(studyCreateRequest.getNumberOfPeople());
        assertThat(result.getCurrentNumberOfPeople()).isEqualTo(1);
        assertThat(result.isOffline()).isEqualTo(true);
        assertThat(result.isOnline()).isEqualTo(true);

        then(categoryRepository).should(times(1)).findWithParentById(any());
        then(locationServiceClient).should(times(1)).findLocationByCode(any());
        then(amazonS3Client).should(times(1)).putObject(any());
        then(amazonS3Client).should(times(2)).getUrl(any(),any());
        then(tagRepository).should(times(1)).findByNameIn(any());
        then(tagRepository).should(times(1)).save(any());
        then(studyRepository).should(times(1)).save(any());
    }

    @Test
    @DisplayName("스터디 수정 - 지역 코드 오류")
    void updateStudyNotWithLocationCode(){
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "스터디이미지.png",
                MediaType.IMAGE_PNG_VALUE,
                "<<이미지>>".getBytes(StandardCharsets.UTF_8));

        StudyUpdateRequest studyUpdateRequest = new StudyUpdateRequest();
        studyUpdateRequest.setName("노드 스터디");
        studyUpdateRequest.setNumberOfPeople(10);
        studyUpdateRequest.setContent("노드 스터디입니다.");
        studyUpdateRequest.setOnline(true);
        studyUpdateRequest.setOffline(true);
        studyUpdateRequest.setTags(Arrays.asList("노드","자바스크립트"));
        studyUpdateRequest.setClose(false);
        studyUpdateRequest.setDeleteImage(false);
        studyUpdateRequest.setCategoryId(2L);
        studyUpdateRequest.setLocationCode(null);

        Category parentCategory = Category.createCategory("개발", null);
        Category childCategory = Category.createCategory("백엔드", parentCategory);

        StudyUser studyUser = StudyUser.createStudyUser(1L, Role.ADMIN);

        List<Tag> tagList = new ArrayList<>();
        Tag tag1 = Tag.createTag("스프링");
        Tag tag2 = Tag.createTag("JPA");
        tagList.add(tag1);
        tagList.add(tag2);

        List<StudyTag> studyTagList = new ArrayList<>();
        StudyTag studyTag1 = StudyTag.createStudyTag(tag1);
        StudyTag studyTag2 = StudyTag.createStudyTag(tag2);
        studyTagList.add(studyTag1);
        studyTagList.add(studyTag2);

        Study study = Study.createStudy("스프링 스터디",
                5, "안녕하세요 스프링 스터디입니다.",
                true, true, "이미지 저장 이름",
                "이미지", "썸네일 이미지", 1L, childCategory, studyUser, studyTagList);

        given(studyQueryRepository.findWithStudyTagsById(any()))
                .willReturn(study);

        given(studyUserRepository.findByUserIdAndAndRoleAndStudy(any(),any(),any()))
                .willReturn(Optional.of(studyUser));

        given(categoryRepository.findWithParentById(any()))
                .willReturn(Optional.of(childCategory));

        assertThrows(StudyException.class,()->studyService.update(1L,1L,image,studyUpdateRequest));

    }
    @Test
    @DisplayName("스터디 수정 - 현재 인원 오류")
    void updateStudyNotValidPeopleOfNumber() throws MalformedURLException {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "스터디이미지.png",
                MediaType.IMAGE_PNG_VALUE,
                "<<이미지>>".getBytes(StandardCharsets.UTF_8));

        StudyUpdateRequest studyUpdateRequest = new StudyUpdateRequest();
        studyUpdateRequest.setName("노드 스터디");
        studyUpdateRequest.setNumberOfPeople(0);
        studyUpdateRequest.setContent("노드 스터디입니다.");
        studyUpdateRequest.setOnline(true);
        studyUpdateRequest.setOffline(true);
        studyUpdateRequest.setTags(Arrays.asList("노드","자바스크립트"));
        studyUpdateRequest.setClose(false);
        studyUpdateRequest.setDeleteImage(false);
        studyUpdateRequest.setCategoryId(2L);
        studyUpdateRequest.setLocationCode("1111051500");

        LocationResponse locationResponse = new LocationResponse();
        locationResponse.setId(1L);
        locationResponse.setCity("서울특별시");
        locationResponse.setCode("1111051500");
        locationResponse.setGu("종로구");
        locationResponse.setDong("삼청동");
        locationResponse.setRi("--리");
        locationResponse.setLen(126.980996);
        locationResponse.setLet(37.590758);
        locationResponse.setCodeType("H");

        Category parentCategory = Category.createCategory("개발", null);
        Category childCategory = Category.createCategory("백엔드", parentCategory);

        StudyUser studyUser = StudyUser.createStudyUser(1L, Role.ADMIN);

        List<Tag> tagList = new ArrayList<>();
        Tag tag1 = Tag.createTestTag(1L,"스프링");
        Tag tag2 = Tag.createTestTag(2L,"JPA");
        tagList.add(tag1);
        tagList.add(tag2);

        List<Tag> changeTagList = new ArrayList<>();
        Tag tag3 = Tag.createTestTag(3L,"노드");
        Tag tag4 = Tag.createTestTag(4L,"자바스크립트");
        changeTagList.add(tag3);
        changeTagList.add(tag4);

        List<StudyTag> studyTagList = new ArrayList<>();
        StudyTag studyTag1 = StudyTag.createStudyTag(tag1);
        StudyTag studyTag2 = StudyTag.createStudyTag(tag2);
        studyTagList.add(studyTag1);
        studyTagList.add(studyTag2);

        Study study = Study.createStudy("스프링 스터디",
                5, "안녕하세요 스프링 스터디입니다.",
                true, true, "이미지 저장 이름",
                "이미지", "썸네일 이미지", 1L, childCategory, studyUser, studyTagList);

        given(studyQueryRepository.findWithStudyTagsById(any()))
                .willReturn(study);

        given(studyUserRepository.findByUserIdAndAndRoleAndStudy(any(),any(),any()))
                .willReturn(Optional.of(studyUser));

        given(categoryRepository.findWithParentById(any()))
                .willReturn(Optional.of(childCategory));

        given(locationServiceClient.findLocationByCode(any()))
                .willReturn(locationResponse);

        willDoNothing()
                .given(amazonS3Client)
                .deleteObject(any(),any());

        given(amazonS3Client.putObject(any()))
                .willReturn(null);

        given(amazonS3Client.getUrl(any(),any()))
                .willReturn(new URL("http:이미지"))
                .willReturn(new URL("http:썸네일이미지"));

        given(tagRepository.findByNameIn(any()))
                .willReturn(changeTagList);

        assertThrows(StudyException.class,()->studyService.update(1L,1L,image,studyUpdateRequest));
    }

    @Test
    @DisplayName("스터디 수정 - 이미지X,오프라인X")
    void updateStudyNotWithImageAndLocation(){
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "스터디이미지.png",
                MediaType.IMAGE_PNG_VALUE,
                "".getBytes(StandardCharsets.UTF_8));

        StudyUpdateRequest studyUpdateRequest = new StudyUpdateRequest();
        studyUpdateRequest.setName("노드 스터디");
        studyUpdateRequest.setNumberOfPeople(10);
        studyUpdateRequest.setContent("노드 스터디입니다.");
        studyUpdateRequest.setOnline(true);
        studyUpdateRequest.setOffline(false);
        studyUpdateRequest.setTags(Arrays.asList("노드","자바스크립트"));
        studyUpdateRequest.setClose(true);
        studyUpdateRequest.setDeleteImage(true);
        studyUpdateRequest.setCategoryId(2L);
        studyUpdateRequest.setLocationCode(null);

        Category parentCategory = Category.createCategory("개발", null);
        Category childCategory = Category.createCategory("백엔드", parentCategory);

        StudyUser studyUser = StudyUser.createStudyUser(1L, Role.ADMIN);

        Tag tag1 = Tag.createTestTag(1L,"스프링");
        Tag tag2 = Tag.createTestTag(2L,"JPA");

        List<StudyTag> studyTagList = new ArrayList<>();
        StudyTag studyTag1 = StudyTag.createStudyTag(tag1);
        StudyTag studyTag2 = StudyTag.createStudyTag(tag2);
        studyTagList.add(studyTag1);
        studyTagList.add(studyTag2);

        List<Tag> changeTagList = new ArrayList<>();
        Tag tag3 = Tag.createTestTag(3L,"노드");
        Tag tag4 = Tag.createTestTag(4L,"자바스크립트");
        changeTagList.add(tag3);
        changeTagList.add(tag4);


        Study study = Study.createStudy("스프링 스터디",
                5, "안녕하세요 스프링 스터디입니다.",
                true, true, "이미지 저장 이름",
                "이미지", "썸네일 이미지", 1L, childCategory, studyUser, studyTagList);

        given(studyQueryRepository.findWithStudyTagsById(any()))
                .willReturn(study);

        given(studyUserRepository.findByUserIdAndAndRoleAndStudy(any(),any(),any()))
                .willReturn(Optional.of(studyUser));

        given(categoryRepository.findWithParentById(any()))
                .willReturn(Optional.of(childCategory));

        willDoNothing()
                .given(amazonS3Client)
                .deleteObject(any(),any());

        given(tagRepository.findByNameIn(any()))
                .willReturn(changeTagList);

        // when
        StudyResponse result = studyService.update(1L, 1L, image, studyUpdateRequest);

        // then
        assertThat(result.getName()).isEqualTo(studyUpdateRequest.getName());
        assertThat(result.getNumberOfPeople()).isEqualTo(studyUpdateRequest.getNumberOfPeople());
        assertThat(result.getChildCategory().getName()).isEqualTo(childCategory.getName());
        assertThat(result.getContent()).isEqualTo(studyUpdateRequest.getContent());
        assertThat(result.getStatus()).isEqualTo(StudyStatus.CLOSE);
        assertThat(result.getLocation().getId()).isNull();
        assertThat(result.getStudyTags()).containsAll(studyUpdateRequest.getTags());
        assertThat(result.getStudyThumbnailImage()).isNull();
        assertThat(result.getStudyImage()).isNull();
        assertThat(result.getParentCategory().getName()).isEqualTo(parentCategory.getName());
        assertThat(result.isOnline()).isEqualTo(studyUpdateRequest.isOnline());
        assertThat(result.isOffline()).isEqualTo(studyUpdateRequest.isOffline());

        then(studyQueryRepository).should(times(1)).findWithStudyTagsById(any());
        then(studyUserRepository).should(times(1)).findByUserIdAndAndRoleAndStudy(any(),any(),any());
        then(categoryRepository).should(times(1)).findWithParentById(any());
        then(amazonS3Client).should(times(2)).deleteObject(any(),any());
        then(tagRepository).should(times(1)).findByNameIn(any());
    }

    @Test
    @DisplayName("스터디 수정 - 이미지O,오프라인O")
    void updateStudyWithImageAndLocation() throws MalformedURLException {
        // given
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "스터디이미지.png",
                MediaType.IMAGE_PNG_VALUE,
                "<<이미지>>".getBytes(StandardCharsets.UTF_8));

        StudyUpdateRequest studyUpdateRequest = new StudyUpdateRequest();
        studyUpdateRequest.setName("노드 스터디");
        studyUpdateRequest.setNumberOfPeople(10);
        studyUpdateRequest.setContent("노드 스터디입니다.");
        studyUpdateRequest.setOnline(true);
        studyUpdateRequest.setOffline(true);
        studyUpdateRequest.setTags(Arrays.asList("노드","자바스크립트"));
        studyUpdateRequest.setClose(false);
        studyUpdateRequest.setDeleteImage(false);
        studyUpdateRequest.setCategoryId(2L);
        studyUpdateRequest.setLocationCode("1111051500");

        LocationResponse locationResponse = new LocationResponse();
        locationResponse.setId(1L);
        locationResponse.setCity("서울특별시");
        locationResponse.setCode("1111051500");
        locationResponse.setGu("종로구");
        locationResponse.setDong("삼청동");
        locationResponse.setRi("--리");
        locationResponse.setLen(126.980996);
        locationResponse.setLet(37.590758);
        locationResponse.setCodeType("H");

        Category parentCategory = Category.createCategory("개발", null);
        Category childCategory = Category.createCategory("백엔드", parentCategory);

        StudyUser studyUser = StudyUser.createStudyUser(1L, Role.ADMIN);

        List<Tag> tagList = new ArrayList<>();
        Tag tag1 = Tag.createTestTag(1L,"스프링");
        Tag tag2 = Tag.createTestTag(2L,"JPA");
        tagList.add(tag1);
        tagList.add(tag2);

        List<Tag> changeTagList = new ArrayList<>();
        Tag tag3 = Tag.createTestTag(3L,"노드");
        Tag tag4 = Tag.createTestTag(4L,"자바스크립트");
        changeTagList.add(tag3);
        changeTagList.add(tag4);

        List<StudyTag> studyTagList = new ArrayList<>();
        StudyTag studyTag1 = StudyTag.createStudyTag(tag1);
        StudyTag studyTag2 = StudyTag.createStudyTag(tag2);
        studyTagList.add(studyTag1);
        studyTagList.add(studyTag2);

        Study study = Study.createStudy("스프링 스터디",
                5, "안녕하세요 스프링 스터디입니다.",
                true, true, "이미지 저장 이름",
                "이미지", "썸네일 이미지", 1L, childCategory, studyUser, studyTagList);

        given(studyQueryRepository.findWithStudyTagsById(any()))
                .willReturn(study);

        given(studyUserRepository.findByUserIdAndAndRoleAndStudy(any(),any(),any()))
                .willReturn(Optional.of(studyUser));

        given(categoryRepository.findWithParentById(any()))
                .willReturn(Optional.of(childCategory));

        given(locationServiceClient.findLocationByCode(any()))
                .willReturn(locationResponse);

        willDoNothing()
                .given(amazonS3Client)
                .deleteObject(any(),any());

        given(amazonS3Client.putObject(any()))
                .willReturn(null);

        given(amazonS3Client.getUrl(any(),any()))
                .willReturn(new URL("http:이미지"))
                .willReturn(new URL("http:썸네일이미지"));

        given(tagRepository.findByNameIn(any()))
                .willReturn(changeTagList);

        // when
        StudyResponse result = studyService.update(1L, 1L, image, studyUpdateRequest);

        // then
        assertThat(result.getName()).isEqualTo(studyUpdateRequest.getName());
        assertThat(result.getNumberOfPeople()).isEqualTo(studyUpdateRequest.getNumberOfPeople());
        assertThat(result.getChildCategory().getName()).isEqualTo(childCategory.getName());
        assertThat(result.getContent()).isEqualTo(studyUpdateRequest.getContent());
        assertThat(result.getStatus()).isEqualTo(StudyStatus.OPEN);
        assertThat(result.getLocation().getId()).isEqualTo(1L);
        assertThat(result.getStudyTags()).containsAll(studyUpdateRequest.getTags());
        assertThat(result.getStudyThumbnailImage()).isEqualTo("http:썸네일이미지");
        assertThat(result.getStudyImage()).isEqualTo("http:이미지");
        assertThat(result.getParentCategory().getName()).isEqualTo(parentCategory.getName());
        assertThat(result.isOnline()).isEqualTo(studyUpdateRequest.isOnline());
        assertThat(result.isOffline()).isEqualTo(studyUpdateRequest.isOffline());

        then(studyQueryRepository).should(times(1)).findWithStudyTagsById(any());
        then(studyUserRepository).should(times(1)).findByUserIdAndAndRoleAndStudy(any(),any(),any());
        then(locationServiceClient).should(times(1)).findLocationByCode(any());
        then(categoryRepository).should(times(1)).findWithParentById(any());
        then(amazonS3Client).should(times(1)).putObject(any());
        then(amazonS3Client).should(times(2)).deleteObject(any(),any());
        then(tagRepository).should(times(1)).findByNameIn(any());
    }

    @Test
    @DisplayName("스터디 삭제 - 스터디 권한 X")
    void deleteNotWithAdminRole(){
        // given
        Category parentCategory = Category.createCategory("개발", null);
        Category childCategory = Category.createCategory("백엔드", parentCategory);

        StudyUser studyUser = StudyUser.createStudyUser(1L, Role.ADMIN);

        List<Tag> tagList = new ArrayList<>();
        Tag tag1 = Tag.createTestTag(1L,"스프링");
        Tag tag2 = Tag.createTestTag(2L,"JPA");
        tagList.add(tag1);
        tagList.add(tag2);

        List<StudyTag> studyTagList = new ArrayList<>();
        StudyTag studyTag1 = StudyTag.createStudyTag(tag1);
        StudyTag studyTag2 = StudyTag.createStudyTag(tag2);
        studyTagList.add(studyTag1);
        studyTagList.add(studyTag2);

        Study study = Study.createStudy("스프링 스터디",
                5, "안녕하세요 스프링 스터디입니다.",
                true, true, "이미지 저장 이름",
                "이미지", "썸네일 이미지", 1L, childCategory, studyUser, studyTagList);

        given(studyQueryRepository.findWithStudyUsersById(any()))
                .willReturn(study);

        // when then
        assertThrows(StudyException.class,()->studyService.delete(2L,1L));
    }

    @Test
    @DisplayName("스터디 삭제 - 스터디 권한 O")
    void deleteWithAdminRole(){
        // given
        Category parentCategory = Category.createCategory("개발", null);
        Category childCategory = Category.createCategory("백엔드", parentCategory);

        StudyUser studyUser = StudyUser.createStudyUser(1L, Role.ADMIN);

        List<Tag> tagList = new ArrayList<>();
        Tag tag1 = Tag.createTestTag(1L,"스프링");
        Tag tag2 = Tag.createTestTag(2L,"JPA");
        tagList.add(tag1);
        tagList.add(tag2);

        List<StudyTag> studyTagList = new ArrayList<>();
        StudyTag studyTag1 = StudyTag.createStudyTag(tag1);
        StudyTag studyTag2 = StudyTag.createStudyTag(tag2);
        studyTagList.add(studyTag1);
        studyTagList.add(studyTag2);

        Study study = Study.createStudy("스프링 스터디",
                5, "안녕하세요 스프링 스터디입니다.",
                true, true, "이미지 저장 이름",
                "이미지", "썸네일 이미지", 1L, childCategory, studyUser, studyTagList);


        given(studyQueryRepository.findWithStudyUsersById(any()))
                .willReturn(study);

        willDoNothing()
                .given(studyRepository)
                .delete(any());

        willDoNothing()
                .given(kafkaStudyDeleteMessageSender)
                .send(any());

        // when
        studyService.delete(1L,1L);

        //  then
        then(studyQueryRepository).should(times(1)).findWithStudyUsersById(any());
        then(studyRepository).should(times(1)).delete(any());
        then(kafkaStudyDeleteMessageSender).should(times(1)).send(any());
    }

    @Test
    @DisplayName("스터디 상세 조회 - 지역정보 포함")
    void findByIdWithLocation(){
        // given
        Category parentCategory = Category.createCategory("개발", null);
        Category childCategory = Category.createCategory("백엔드", parentCategory);

        StudyUser studyUser = StudyUser.createStudyUser(1L, Role.ADMIN);

        List<Tag> tagList = new ArrayList<>();
        Tag tag1 = Tag.createTestTag(1L,"스프링");
        Tag tag2 = Tag.createTestTag(2L,"JPA");
        tagList.add(tag1);
        tagList.add(tag2);

        List<StudyTag> studyTagList = new ArrayList<>();
        StudyTag studyTag1 = StudyTag.createStudyTag(tag1);
        StudyTag studyTag2 = StudyTag.createStudyTag(tag2);
        studyTagList.add(studyTag1);
        studyTagList.add(studyTag2);

        Study study = Study.createStudy("스프링 스터디",
                5, "안녕하세요 스프링 스터디입니다.",
                true, true, "이미지 저장 이름",
                "이미지", "썸네일 이미지", 1L, childCategory, studyUser, studyTagList);

        LocationResponse locationResponse = new LocationResponse();
        locationResponse.setId(1L);
        locationResponse.setCity("서울특별시");
        locationResponse.setCode("1111051500");
        locationResponse.setGu("종로구");
        locationResponse.setDong("삼청동");
        locationResponse.setRi("--리");
        locationResponse.setLen(126.980996);
        locationResponse.setLet(37.590758);
        locationResponse.setCodeType("H");

        given(studyQueryRepository.findWithCategoryAndStudyTagsAndTagById(any()))
                .willReturn(study);

        given(locationServiceClient.findLocationById(any()))
                .willReturn(locationResponse);

        StudyResponse studyResponse = studyService.findById(1L);

        assertThat(studyResponse.getName()).isEqualTo(study.getName());
        assertThat(studyResponse.getStudyTags().size()).isEqualTo(2);
        assertThat(studyResponse.getStudyTags()).contains("스프링","JPA");
        assertThat(studyResponse.getLocation().getId()).isEqualTo(1L);
        assertThat(studyResponse.getChildCategory().getName()).isEqualTo("백엔드");
        assertThat(studyResponse.getParentCategory().getName()).isEqualTo("개발");

        then(studyQueryRepository).should(times(1)).findWithCategoryAndStudyTagsAndTagById(any());
        then(locationServiceClient).should(times(1)).findLocationById(any());
    }

    @Test
    @DisplayName("스터디 상세 조회 - 지역정보 미포함")
    void findByIdNotWithLocation(){
        // given
        Category parentCategory = Category.createCategory("개발", null);
        Category childCategory = Category.createCategory("백엔드", parentCategory);

        StudyUser studyUser = StudyUser.createStudyUser(1L, Role.ADMIN);

        List<Tag> tagList = new ArrayList<>();
        Tag tag1 = Tag.createTestTag(1L,"스프링");
        Tag tag2 = Tag.createTestTag(2L,"JPA");
        tagList.add(tag1);
        tagList.add(tag2);

        List<StudyTag> studyTagList = new ArrayList<>();
        StudyTag studyTag1 = StudyTag.createStudyTag(tag1);
        StudyTag studyTag2 = StudyTag.createStudyTag(tag2);
        studyTagList.add(studyTag1);
        studyTagList.add(studyTag2);

        Study study = Study.createStudy("스프링 스터디",
                5, "안녕하세요 스프링 스터디입니다.",
                true, true, "이미지 저장 이름",
                "이미지", "썸네일 이미지", null, childCategory, studyUser, studyTagList);

        given(studyQueryRepository.findWithCategoryAndStudyTagsAndTagById(any()))
                .willReturn(study);

        // when
        StudyResponse studyResponse = studyService.findById(1L);

        // then
        assertThat(studyResponse.getName()).isEqualTo(study.getName());
        assertThat(studyResponse.getStudyTags().size()).isEqualTo(2);
        assertThat(studyResponse.getStudyTags()).contains("스프링","JPA");
        assertThat(studyResponse.getLocation().getId()).isEqualTo(null);
        assertThat(studyResponse.getChildCategory().getName()).isEqualTo("백엔드");
        assertThat(studyResponse.getParentCategory().getName()).isEqualTo("개발");

        then(studyQueryRepository).should(times(1)).findWithCategoryAndStudyTagsAndTagById(any());
    }

    @Test
    @DisplayName("스터디 참가 신청 인원 추가 - 이미 가입한 인원일 경우")
    void createWaitUserDuplicateStudyUser(){
        // given
        Category parentCategory = Category.createCategory("개발", null);
        Category childCategory = Category.createCategory("백엔드", parentCategory);

        StudyUser studyUser = StudyUser.createStudyUser(1L, Role.ADMIN);

        List<Tag> tagList = new ArrayList<>();
        Tag tag1 = Tag.createTestTag(1L,"스프링");
        Tag tag2 = Tag.createTestTag(2L,"JPA");
        tagList.add(tag1);
        tagList.add(tag2);

        List<StudyTag> studyTagList = new ArrayList<>();
        StudyTag studyTag1 = StudyTag.createStudyTag(tag1);
        StudyTag studyTag2 = StudyTag.createStudyTag(tag2);
        studyTagList.add(studyTag1);
        studyTagList.add(studyTag2);

        Study study = Study.createStudy("스프링 스터디",
                5, "안녕하세요 스프링 스터디입니다.",
                true, true, "이미지 저장 이름",
                "이미지", "썸네일 이미지", null, childCategory, studyUser, studyTagList);

        given(studyQueryRepository.findWithWaitUserById(any()))
                .willReturn(study);

        given(studyUserRepository.findByUserIdAndStudy(any(),any()))
                .willReturn(Optional.of(studyUser));

        assertThrows(StudyException.class,()->studyService.createWaitUser(1L,1L));
    }

    @Test
    @DisplayName("스터디 참가 신청 인원 추가 - 이미 참가 신청을 한 인원일 경우")
    void createDuplicatedWaitUser(){
        // given
        Category parentCategory = Category.createCategory("개발", null);
        Category childCategory = Category.createCategory("백엔드", parentCategory);

        StudyUser studyUser = StudyUser.createStudyUser(1L, Role.ADMIN);

        List<Tag> tagList = new ArrayList<>();
        Tag tag1 = Tag.createTestTag(1L,"스프링");
        Tag tag2 = Tag.createTestTag(2L,"JPA");
        tagList.add(tag1);
        tagList.add(tag2);

        List<StudyTag> studyTagList = new ArrayList<>();
        StudyTag studyTag1 = StudyTag.createStudyTag(tag1);
        StudyTag studyTag2 = StudyTag.createStudyTag(tag2);
        studyTagList.add(studyTag1);
        studyTagList.add(studyTag2);

        Study study = Study.createStudy("스프링 스터디",
                5, "안녕하세요 스프링 스터디입니다.",
                true, true, "이미지 저장 이름",
                "이미지", "썸네일 이미지", null, childCategory, studyUser, studyTagList);
        study.addWaitUser(2L);

        given(studyQueryRepository.findWithWaitUserById(any()))
                .willReturn(study);

        given(studyUserRepository.findByUserIdAndStudy(any(),any()))
                .willReturn(Optional.empty());

        assertThrows(StudyException.class,()->studyService.createWaitUser(2L,1L));
    }

    @Test
    @DisplayName("스터디 참가 인원 추가 - 성공")
    void addWaitUser(){
        // given
        Category parentCategory = Category.createCategory("개발", null);
        Category childCategory = Category.createCategory("백엔드", parentCategory);

        StudyUser studyUser = StudyUser.createStudyUser(1L, Role.ADMIN);

        List<Tag> tagList = new ArrayList<>();
        Tag tag1 = Tag.createTestTag(1L,"스프링");
        Tag tag2 = Tag.createTestTag(2L,"JPA");
        tagList.add(tag1);
        tagList.add(tag2);

        List<StudyTag> studyTagList = new ArrayList<>();
        StudyTag studyTag1 = StudyTag.createStudyTag(tag1);
        StudyTag studyTag2 = StudyTag.createStudyTag(tag2);
        studyTagList.add(studyTag1);
        studyTagList.add(studyTag2);

        Study study = Study.createStudy("스프링 스터디",
                5, "안녕하세요 스프링 스터디입니다.",
                true, true, "이미지 저장 이름",
                "이미지", "썸네일 이미지", null, childCategory, studyUser, studyTagList);

        given(studyQueryRepository.findWithWaitUserById(any()))
                .willReturn(study);

        given(studyUserRepository.findByUserIdAndStudy(any(),any()))
                .willReturn(Optional.empty());

        willDoNothing()
                .given(kafkaStudyJoinMessageSender)
                .send(any());

        studyService.createWaitUser(2L,1L);

        assertThat(study.getWaitUsers().size()).isEqualTo(1);
        assertThat(study.getWaitUsers().get(0).getUserId()).isEqualTo(2L);
        then(studyQueryRepository).should(times(1)).findWithWaitUserById(any());
        then(studyUserRepository).should(times(1)).findByUserIdAndStudy(any(),any());
        then(kafkaStudyJoinMessageSender).should(times(1)).send(any());
    }
}
