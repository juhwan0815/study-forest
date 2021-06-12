package com.study.studyservice.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.study.studyservice.client.LocationServiceClient;
import com.study.studyservice.domain.*;
import com.study.studyservice.exception.StudyException;
import com.study.studyservice.model.location.response.LocationResponse;
import com.study.studyservice.model.study.request.StudyCreateRequest;
import com.study.studyservice.model.study.response.StudyResponse;
import com.study.studyservice.repository.CategoryRepository;
import com.study.studyservice.repository.StudyRepository;
import com.study.studyservice.repository.TagRepository;
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
    private AmazonS3Client amazonS3Client;

    @Test
    @DisplayName("스터디 생성 - 오류")
    void createStudyNotWithLocationCode(){
        // given
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "프로필사진.png",
                "image/png",
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
                "프로필사진.png",
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
                "프로필사진.png",
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
}
