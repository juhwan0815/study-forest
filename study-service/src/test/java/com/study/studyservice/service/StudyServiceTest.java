package com.study.studyservice.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.study.studyservice.client.LocationServiceClient;
import com.study.studyservice.client.UserServiceClient;
import com.study.studyservice.domain.Study;
import com.study.studyservice.domain.StudyStatus;
import com.study.studyservice.exception.StudyException;
import com.study.studyservice.kafka.sender.StudyApplySuccessMessageSender;
import com.study.studyservice.kafka.sender.StudyDeleteMessageSender;
import com.study.studyservice.kafka.sender.StudyApplyCreateMessageSender;
import com.study.studyservice.model.study.response.StudyResponse;
import com.study.studyservice.model.waituser.WaitUserResponse;
import com.study.studyservice.repository.CategoryRepository;
import com.study.studyservice.repository.StudyRepository;
import com.study.studyservice.repository.query.StudyQueryRepository;
import com.study.studyservice.service.impl.StudyServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import scala.Array;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.study.studyservice.fixture.CategoryFixture.*;
import static com.study.studyservice.fixture.StudyFixture.*;
import static com.study.studyservice.fixture.TagFixture.*;
import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class StudyServiceTest {

    @InjectMocks
    private StudyServiceImpl studyService;

    @Mock
    private StudyRepository studyRepository;

    @Mock
    private TagService tagService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private StudyQueryRepository studyQueryRepository;

    @Mock
    private LocationServiceClient locationServiceClient;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private AmazonS3Client amazonS3Client;

    @Mock
    private StudyDeleteMessageSender studyDeleteMessageSender;

    @Mock
    private StudyApplyCreateMessageSender studyApplyCreateMessageSender;

    @Mock
    private StudyApplySuccessMessageSender studyApplySuccessMessageSender;

    @Test
    @DisplayName("예외테스트 : 동네정보 코드 없이 오프라인 스터디 생성 요청을 보낼 경우 예외가 발생한다.")
    void createStudyNotWithLocationCode(){
        // given
        given(categoryRepository.findWithParentById(any()))
                .willReturn(Optional.of(TEST_CATEGORY2));

        // when
        assertThrows(StudyException.class,()->
                studyService.create(1L, TEST_IMAGE_FILE,TEST_STUDY_CREATE_REQUEST2));
    }

    @Test
    @DisplayName("스터디 이미지 없이 온라인으로 스터디를 생성한다.")
    void createStudyNotWithImageAndLocation() {
        // given
        given(categoryRepository.findWithParentById(any()))
                .willReturn(Optional.of(TEST_CATEGORY2));

        given(tagService.FindAndCreate(any()))
                .willReturn(TEST_TAG_LIST);

        given(studyRepository.save(any()))
                .willReturn(createTestOnlineStudy());

        // when
        StudyResponse result = studyService.create(1L,TEST_IMAGE_EMPTY_FILE,TEST_STUDY_CREATE_REQUEST3);

        // then
        assertThat(result.getName()).isEqualTo(TEST_STUDY_CREATE_REQUEST3.getName());
        assertThat(result.getContent()).isEqualTo(TEST_STUDY_CREATE_REQUEST3.getContent());
        assertThat(result.getLocation().getId()).isEqualTo(null);
        assertThat(result.getImage()).isNull();
        assertThat(result.getNumberOfPeople()).isEqualTo(TEST_STUDY_CREATE_REQUEST3.getNumberOfPeople());
        assertThat(result.getCurrentNumberOfPeople()).isEqualTo(1);
        assertThat(result.isOffline()).isEqualTo(false);
        assertThat(result.isOnline()).isEqualTo(true);
        assertThat(result.getStatus()).isEqualTo(StudyStatus.OPEN);

        assertThat(result.getParentCategory().getId()).isEqualTo(TEST_CATEGORY1.getId());
        assertThat(result.getChildCategory().getId()).isEqualTo(TEST_CATEGORY2.getId());

        assertThat(result.getStudyTags().size()).isEqualTo(2);
        assertThat(result.getStudyTags()).contains("스프링","JPA");

        then(categoryRepository).should(times(1)).findWithParentById(any());
        then(tagService).should(times(1)).FindAndCreate(any());
        then(studyRepository).should(times(1)).save(any());
    }

    @Test
    @DisplayName("예외테스트 : 스터디 이미지 파일 타입이 이미지가 아닐 경우 예외가 발생한다.")
    void createStudyNotValidImageType(){
        // given
        given(categoryRepository.findWithParentById(any()))
                .willReturn(Optional.of(TEST_CATEGORY2));

        given(tagService.FindAndCreate(any()))
                .willReturn(TEST_TAG_LIST);

        // when
        assertThrows(StudyException.class,()->
                studyService.create(1L, TEST_FILE,TEST_STUDY_CREATE_REQUEST3));
    }

    @Test
    @DisplayName("스터디 이미지가 있고 오프라인을 지원하는 스터디를 생성한다.")
    void createStudyWithImageAndLocation() throws Exception {
        // given
        given(categoryRepository.findWithParentById(any()))
                .willReturn(Optional.of(TEST_CATEGORY2));

        given(locationServiceClient.findLocationByCode(any()))
                .willReturn(TEST_LOCATION_RESPONSE);

        given(tagService.FindAndCreate(any()))
                .willReturn(TEST_TAG_LIST);

        given(amazonS3Client.putObject(any()))
                .willReturn(null);

        given(amazonS3Client.getUrl(any(),any()))
                .willReturn(new URL("http:이미지"))
                .willReturn(new URL("http:썸네일이미지"));

        given(studyRepository.save(any()))
                .willReturn(createTestOfflineStudy());

        // when
        StudyResponse result = studyService.create(1L,TEST_IMAGE_FILE,TEST_STUDY_CREATE_REQUEST1);

        // then
        assertThat(result.getName()).isEqualTo(TEST_STUDY_CREATE_REQUEST3.getName());
        assertThat(result.getContent()).isEqualTo(TEST_STUDY_CREATE_REQUEST3.getContent());
        assertThat(result.getLocation()).isEqualTo(TEST_LOCATION_RESPONSE);
        assertThat(result.getImage()).isEqualTo(TEST_IMAGE);
        assertThat(result.getNumberOfPeople()).isEqualTo(TEST_STUDY_CREATE_REQUEST3.getNumberOfPeople());
        assertThat(result.getCurrentNumberOfPeople()).isEqualTo(1);
        assertThat(result.isOffline()).isEqualTo(true);
        assertThat(result.isOnline()).isEqualTo(true);
        assertThat(result.getStatus()).isEqualTo(StudyStatus.OPEN);

        assertThat(result.getParentCategory().getId()).isEqualTo(TEST_CATEGORY1.getId());
        assertThat(result.getChildCategory().getId()).isEqualTo(TEST_CATEGORY2.getId());

        assertThat(result.getStudyTags().size()).isEqualTo(2);
        assertThat(result.getStudyTags()).contains("스프링","JPA");

        then(categoryRepository).should(times(1)).findWithParentById(any());
        then(locationServiceClient).should(times(1)).findLocationByCode(any());
        then(tagService).should(times(1)).FindAndCreate(any());
        then(amazonS3Client).should(times(1)).putObject(any());
        then(amazonS3Client).should(times(2)).getUrl(any(),any());
        then(studyRepository).should(times(1)).save(any());
    }

    @Test
    @DisplayName("예외테스트 : 스터디의 현재 인원이 수정할 인원보다 많을 경우 예외가 발생한다.")
    void updateStudyNotValidPeopleOfNumber(){
        given(studyQueryRepository.findWithStudyTagsById(any()))
                .willReturn(createTestOnlineStudy());

        assertThrows(StudyException.class,
                ()->studyService.update(1L,1L,TEST_IMAGE_FILE,TEST_STUDY_UPDATE_REQUEST1));
    }

    @Test
    @DisplayName("예외테스트 : 스터디 관리자가 아닌 회원이 스터디를 수정할 경우 예외가 발생한다.")
    void updateStudyNotStudyAdminUser(){
        given(studyQueryRepository.findWithStudyTagsById(any()))
                .willReturn(createTestOnlineStudy());

        assertThrows(StudyException.class,
                ()->studyService.update(2L,1L,TEST_IMAGE_FILE,TEST_STUDY_UPDATE_REQUEST2));
    }

    @Test
    @DisplayName("예외테스트 : 동네정보 코드 없이 오프라인 스터디 수정 요청을 보낼 경우 예외가 발생한다.")
    void updateStudyNotWithLocationCode(){
        given(studyQueryRepository.findWithStudyTagsById(any()))
                .willReturn(createTestOnlineStudy());

        given(categoryRepository.findWithParentById(any()))
                .willReturn(Optional.of(TEST_CATEGORY2));

        assertThrows(StudyException.class,
                ()->studyService.update(1L,1L,TEST_IMAGE_FILE,TEST_STUDY_UPDATE_REQUEST3));
    }

    @Test
    @DisplayName("스터디 이미지가 없고 온라인 스터디로 수정한다.")
    void updateStudyNotWithImageAndLocation(){

        given(studyQueryRepository.findWithStudyTagsById(any()))
                .willReturn(createTestOfflineStudy());

        given(categoryRepository.findWithParentById(any()))
                .willReturn(Optional.of(TEST_CATEGORY2));

        given(tagService.FindAndCreate(any()))
                .willReturn(createTestTagList());

        willDoNothing().
                given(amazonS3Client)
                .deleteObject(any(),any());

        StudyResponse result = studyService.update(1L, 1L, TEST_IMAGE_EMPTY_FILE, TEST_STUDY_UPDATE_REQUEST2);

        assertThat(result.getName()).isEqualTo(TEST_STUDY_UPDATE_REQUEST2.getName());
        assertThat(result.getContent()).isEqualTo(TEST_STUDY_UPDATE_REQUEST2.getContent());
        assertThat(result.getLocation().getId()).isNull();
        assertThat(result.getImage()).isNull();
        assertThat(result.getNumberOfPeople()).isEqualTo(TEST_STUDY_UPDATE_REQUEST2.getNumberOfPeople());
        assertThat(result.getCurrentNumberOfPeople()).isEqualTo(1);
        assertThat(result.isOffline()).isEqualTo(false);
        assertThat(result.isOnline()).isEqualTo(true);
        assertThat(result.getStatus()).isEqualTo(StudyStatus.OPEN);

        assertThat(result.getParentCategory().getId()).isEqualTo(TEST_CATEGORY1.getId());
        assertThat(result.getChildCategory().getId()).isEqualTo(TEST_CATEGORY2.getId());

        assertThat(result.getStudyTags().size()).isEqualTo(2);
        assertThat(result.getStudyTags()).contains("스프링","JPA");

        then(studyQueryRepository).should(times(1)).findWithStudyTagsById(any());
        then(categoryRepository).should(times(1)).findWithParentById(any());
        then(tagService).should(times(1)).FindAndCreate(any());
        then(amazonS3Client).should(times(2)).deleteObject(any(),any());
    }

    @Test
    @DisplayName("스터디 이미지가 있고 오프라인 스터디로 수정한다.")
    void updateStudyWithImageAndLocation() throws Exception {
        // given
        given(studyQueryRepository.findWithStudyTagsById(any()))
                .willReturn(createTestOnlineStudy());

        given(categoryRepository.findWithParentById(any()))
                .willReturn(Optional.of(TEST_CATEGORY2));

        given(locationServiceClient.findLocationByCode(any()))
                .willReturn(TEST_LOCATION_RESPONSE);

        given(tagService.FindAndCreate(any()))
                .willReturn(createTestTagList());

        given(amazonS3Client.putObject(any()))
                .willReturn(null);

        given(amazonS3Client.getUrl(any(),any()))
                .willReturn(new URL("http:이미지"))
                .willReturn(new URL("http:썸네일이미지"));

        // when
        StudyResponse result = studyService.update(1L, 1L, TEST_IMAGE_FILE,TEST_STUDY_UPDATE_REQUEST4 );

        // then
        assertThat(result.getName()).isEqualTo(TEST_STUDY_UPDATE_REQUEST4.getName());
        assertThat(result.getContent()).isEqualTo(TEST_STUDY_UPDATE_REQUEST4.getContent());
        assertThat(result.getLocation()).isEqualTo(TEST_LOCATION_RESPONSE);
        assertThat(result.getImage().getStudyImage()).isEqualTo("http:이미지");
        assertThat(result.getImage().getThumbnailImage()).isEqualTo("http:썸네일이미지");
        assertThat(result.getNumberOfPeople()).isEqualTo(TEST_STUDY_UPDATE_REQUEST4.getNumberOfPeople());
        assertThat(result.getCurrentNumberOfPeople()).isEqualTo(1);
        assertThat(result.isOffline()).isEqualTo(true);
        assertThat(result.isOnline()).isEqualTo(true);
        assertThat(result.getStatus()).isEqualTo(StudyStatus.CLOSE);

        assertThat(result.getParentCategory().getId()).isEqualTo(TEST_CATEGORY1.getId());
        assertThat(result.getChildCategory().getId()).isEqualTo(TEST_CATEGORY2.getId());

        assertThat(result.getStudyTags().size()).isEqualTo(2);
        assertThat(result.getStudyTags()).contains("스프링","JPA");

        then(studyQueryRepository).should(times(1)).findWithStudyTagsById(any());
        then(categoryRepository).should(times(1)).findWithParentById(any());
        then(locationServiceClient).should(times(1)).findLocationByCode(any());
        then(tagService).should(times(1)).FindAndCreate(any());
        then(amazonS3Client).should(times(1)).putObject(any());
        then(amazonS3Client).should(times(2)).getUrl(any(),any());
    }

    @Test
    @DisplayName("예외테스트 : 스터디의 관리자가 아닌 회원이 스터디 삭제 요청을 할 경우 예외가 발생한다.")
    void deleteNotWithAdminRole(){
        // given
        given(studyQueryRepository.findWithStudyUsersById(any()))
                .willReturn(createTestOfflineStudy());

        // when then
        assertThrows(StudyException.class,()->studyService.delete(2L,1L));
    }

    @Test
    @DisplayName("스터디 관리자가 스터디를 삭제한다.")
    void deleteWithAdminRole(){
        // given
        given(studyQueryRepository.findWithStudyUsersById(any()))
                .willReturn(createTestOfflineStudy());

        willDoNothing()
                .given(studyRepository)
                .delete(any());

        willDoNothing()
                .given(studyDeleteMessageSender)
                .send(any());

        // when
        studyService.delete(1L,1L);

        //  then
        then(studyQueryRepository).should(times(1)).findWithStudyUsersById(any());
        then(studyRepository).should(times(1)).delete(any());
        then(studyDeleteMessageSender).should(times(1)).send(any());
    }

    @Test
    @DisplayName("스터디 이미지가 있는 오프라인 스터디를 상세 조회한다.")
    void findByIdWithLocationAndImage(){
        // given
        given(studyQueryRepository.findWithCategoryAndStudyTagsAndTagById(any()))
                .willReturn(createTestOfflineStudy());

        given(locationServiceClient.findLocationById(any()))
                .willReturn(TEST_LOCATION_RESPONSE);

        StudyResponse result = studyService.findById(1L);

        assertThat(result.getName()).isEqualTo("테스트 스터디");
        assertThat(result.getContent()).isEqualTo("테스트 스터디 입니다.");
        assertThat(result.getStudyTags().size()).isEqualTo(2);
        assertThat(result.getStudyTags()).contains("스프링","JPA");
        assertThat(result.getLocation()).isEqualTo(TEST_LOCATION_RESPONSE);
        assertThat(result.getImage()).isEqualTo(TEST_IMAGE);
        assertThat(result.getChildCategory().getName()).isEqualTo("프론트엔드");
        assertThat(result.getParentCategory().getName()).isEqualTo("개발");

        then(studyQueryRepository).should(times(1)).findWithCategoryAndStudyTagsAndTagById(any());
        then(locationServiceClient).should(times(1)).findLocationById(any());
    }

    @Test
    @DisplayName("스터디 이미지가 없는 온라인 스터디를 상세 조회한다.")
    void findByIdNotWithLocationAndImage(){
        // given
        given(studyQueryRepository.findWithCategoryAndStudyTagsAndTagById(any()))
                .willReturn(createTestOnlineStudy());

        // when
        StudyResponse result = studyService.findById(1L);

        // then
        assertThat(result.getName()).isEqualTo("테스트 스터디");
        assertThat(result.getContent()).isEqualTo("테스트 스터디 입니다.");
        assertThat(result.getStudyTags().size()).isEqualTo(2);
        assertThat(result.getStudyTags()).contains("스프링","JPA");
        assertThat(result.getLocation().getId()).isNull();
        assertThat(result.getImage()).isNull();
        assertThat(result.getChildCategory().getName()).isEqualTo("프론트엔드");
        assertThat(result.getParentCategory().getName()).isEqualTo("개발");

        then(studyQueryRepository).should(times(1))
                .findWithCategoryAndStudyTagsAndTagById(any());
    }

    @Test
    @DisplayName("예외테스트 : 이미 스터디의 가입한 회원이 스터디 참가 신청을 할 경우 예외가 발생한다.")
    void createWaitUserDuplicateStudyUser(){
        // given
        given(studyQueryRepository.findWithWaitUserById(any()))
                .willReturn(createTestOfflineStudy());

        assertThrows(StudyException.class,()->studyService.createWaitUser(1L,1L));
    }

    @Test
    @DisplayName("예외테스트 : 이미 스터디 참가 신청을 한 회원이 스터디 참가 신청을 또 할 경우 예외가 발생한다.")
    void createDuplicatedWaitUser(){
        // given
        given(studyQueryRepository.findWithWaitUserById(any()))
                .willReturn(createTestOfflineStudy());

        assertThrows(StudyException.class,()->studyService.createWaitUser(2L,1L));
    }

    @Test
    @DisplayName("회원이 스터디 참가 신청을 한다.")
    void addWaitUser(){
        // given
        Study study = createTestOfflineStudy();
        given(studyQueryRepository.findWithWaitUserById(any()))
                .willReturn(study);

        willDoNothing()
                .given(studyApplyCreateMessageSender)
                .send(any());

        // when
        studyService.createWaitUser(4L,1L);

        // then
        assertThat(study.getWaitUsers().size()).isEqualTo(3);
        assertThat(study.getWaitUsers().get(2).getUserId()).isEqualTo(4L);

        then(studyQueryRepository).should(times(1)).findWithWaitUserById(any());
        then(studyApplyCreateMessageSender).should(times(1)).send(any());
    }

    @Test
    @DisplayName("회원이 스터디 대기 인원을 조회한다.")
    void findWaitUserByStudyId(){
        // given
        given(studyQueryRepository.findWithWaitUserById(any()))
                .willReturn(createTestOfflineStudy());

        given(userServiceClient.findUserByIdIn(any()))
                .willReturn(Arrays.asList(TEST_USER_RESPONSE1,TEST_USER_RESPONSE2));

        // when
        List<WaitUserResponse> result = studyService.findWaitUsersByStudyId(1L);

        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getUserId()).isEqualTo(TEST_USER_RESPONSE2.getId());
        assertThat(result.get(1).getUserId()).isEqualTo(TEST_USER_RESPONSE1.getId());
    }

    @Test
    @DisplayName("예외 테스트 : 스터디 관리자가 아닌 회원이 스터디 참가 신청을 승인하면 예외가 발생한다.")
    void createStudyUserWhenNotStudyAdmin(){
        // given
        given(studyQueryRepository.findWithWaitUserById(any()))
                .willReturn(createTestOfflineStudy());

        // when
        assertThrows(StudyException.class,
                ()->studyService.createStudyUser(2L,1L,2L));
    }

    @Test
    @DisplayName("예외 테스트 : 스터디 관리자가 스터디 참가 신청을 승인할 때 스터디의 상태가 CLOSE 면 예외가 발생한다.")
    void createStudyUserWhenStatusCLOSE(){
        // given
        given(studyQueryRepository.findWithWaitUserById(any()))
                .willReturn(createTestCloseStudy());

        // when
        assertThrows(StudyException.class,()->studyService.createStudyUser(1L,1L,2L));
    }

    @Test
    @DisplayName("스터디 관리자가 스터디 참가 신청을 승인한다.")
    void createStudyUser(){
        // given
        Study study = createTestOfflineStudy();
        given(studyQueryRepository.findWithWaitUserById(any()))
                .willReturn(study);
        willDoNothing()
                .given(studyApplySuccessMessageSender)
                .send(any());

        // when
        studyService.createStudyUser(1L,1L,2L);

        // then
        assertThat(study.getStudyUsers().size()).isEqualTo(2);
        assertThat(study.getStudyUsers().get(1).getUserId()).isEqualTo(2L);
        assertThat(study.getWaitUsers().size()).isEqualTo(1);
        then(studyQueryRepository).should(times(1)).findWithWaitUserById(any());
        then(studyApplySuccessMessageSender).should(times(1)).send(any());
    }

}
