package com.study.service;

import com.study.client.AreaServiceClient;
import com.study.client.AwsClient;
import com.study.client.UserResponse;
import com.study.client.UserServiceClient;
import com.study.domain.ChatRoom;
import com.study.domain.Study;
import com.study.domain.StudyRole;
import com.study.dto.chatroom.ChatRoomResponse;
import com.study.dto.study.StudyCreateRequest;
import com.study.dto.study.StudyResponse;
import com.study.dto.study.StudySearchRequest;
import com.study.dto.study.StudyUpdateRequest;
import com.study.dto.studyuser.StudyUserResponse;
import com.study.exception.AccessDeniedException;
import com.study.exception.NotFoundException;
import com.study.kakfa.sender.*;
import com.study.repository.CategoryRepository;
import com.study.repository.StudyQueryRepository;
import com.study.repository.StudyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.study.CategoryFixture.TEST_CATEGORY;
import static com.study.CategoryFixture.TEST_CHILD_CATEGORY;
import static com.study.CommonFixture.TEST_IMAGE_FILE;
import static com.study.CommonFixture.TEST_SIZE;
import static com.study.StudyFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class StudyServiceTest {

    @InjectMocks
    private StudyServiceImpl studyService;

    @Mock
    private StudyRepository studyRepository;

    @Mock
    private StudyQueryRepository studyQueryRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private AreaServiceClient areaServiceClient;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private AwsClient awsClient;

    @Mock
    private StudyCreateMessageSender studyCreateMessageSender;

    @Mock
    private StudyDeleteMessageSender studyDeleteMessageSender;

    @Mock
    private StudyApplyFailMessageSender studyApplyFailMessageSender;

    @Mock
    private StudyApplySuccessMessageSender studyApplySuccessMessageSender;

    @Mock
    private ChatRoomDeleteMessageSender chatRoomDeleteMessageSender;

    @Test
    @DisplayName("온라인/오프라인을 지원하는 스터디를 생성한다.")
    void create() {
        // given
        given(categoryRepository.findById(any()))
                .willReturn(Optional.of(TEST_CATEGORY));

        given(areaServiceClient.findByCode(any()))
                .willReturn(TEST_AREA_RESPONSE);

        given(studyRepository.save(any()))
                .willReturn(TEST_STUDY);

        willDoNothing()
                .given(studyCreateMessageSender)
                .send(any());

        // when
        Long result = studyService.create(1L, TEST_STUDY_CREATE_REQUEST);

        // then
        assertThat(result).isEqualTo(TEST_STUDY.getId());
        then(categoryRepository).should(times(1)).findById(any());
        then(areaServiceClient).should(times(1)).findByCode(any());
        then(studyRepository).should(times(1)).save(any());
        then(studyCreateMessageSender).should(times(1)).send(any());
    }

    @Test
    @DisplayName("온라인 스터디를 생성한다.")
    void createOnline() {
        // given
        StudyCreateRequest studyCreateRequest
                = new StudyCreateRequest(TEST_STUDY_NAME, TEST_STUDY_NUMBER_OF_PEOPLE, TEST_STUDY_CONTENT, Arrays.asList("스프링", "JPA"), TEST_STUDY_ONLINE, false, TEST_AREA_CODE, TEST_STUDY_IMAGE_URL, TEST_CATEGORY.getId());

        given(categoryRepository.findById(any()))
                .willReturn(Optional.of(TEST_CATEGORY));

        given(studyRepository.save(any()))
                .willReturn(TEST_STUDY);

        willDoNothing()
                .given(studyCreateMessageSender)
                .send(any());

        // when
        Long result = studyService.create(TEST_USER_RESPONSE.getUserId(), studyCreateRequest);

        // then
        assertThat(result).isEqualTo(TEST_STUDY.getId());
        then(categoryRepository).should(times(1)).findById(any());
        then(studyRepository).should(times(1)).save(any());
        then(studyCreateMessageSender).should(times(1)).send(any());
    }

    @Test
    @DisplayName("스터디를 생성할 때 카테고리가 존재하지 않으면 예외가 발생한다.")
    void createNotFound() {
        // given
        given(categoryRepository.findById(any()))
                .willReturn(Optional.empty());

        // when
        assertThrows(NotFoundException.class, () -> studyService.create(TEST_USER_RESPONSE.getUserId(), TEST_STUDY_CREATE_REQUEST));

        // then
        then(categoryRepository).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("스터디를 수정한다.")
    void update() {
        // given
        Study study = Study.createStudy(TEST_STUDY_NAME, TEST_STUDY_CONTENT, TEST_STUDY_NUMBER_OF_PEOPLE, TEST_STUDY_ONLINE, TEST_STUDY_OFFLINE, TEST_STUDY_IMAGE_URL, TEST_CHILD_CATEGORY);
        study.addStudyUser(TEST_USER_RESPONSE.getUserId(), StudyRole.ADMIN);

        given(studyRepository.findWithTagById(any()))
                .willReturn(Optional.of(study));

        given(categoryRepository.findById(any()))
                .willReturn(Optional.of(TEST_CATEGORY));

        given(areaServiceClient.findByCode(any()))
                .willReturn(TEST_AREA_RESPONSE);

        // when
        studyService.update(TEST_USER_RESPONSE.getUserId(), TEST_STUDY.getId(), TEST_STUDY_UPDATE_REQUEST);

        // then
        then(studyRepository).should(times(1)).findWithTagById(any());
        then(categoryRepository).should(times(1)).findById(any());
        then(areaServiceClient).should(times(1)).findByCode(any());
    }

    @Test
    @DisplayName("온라인 스터디로 수정한다.")
    void updateOnline() {
        // given
        Study study = Study.createStudy(TEST_STUDY_NAME, TEST_STUDY_CONTENT, TEST_STUDY_NUMBER_OF_PEOPLE, TEST_STUDY_ONLINE, TEST_STUDY_OFFLINE, TEST_STUDY_IMAGE_URL, TEST_CHILD_CATEGORY);
        study.addStudyUser(TEST_USER_RESPONSE.getUserId(), StudyRole.ADMIN);

        StudyUpdateRequest studyUpdateRequest = new StudyUpdateRequest(TEST_STUDY_NAME, TEST_STUDY_NUMBER_OF_PEOPLE, TEST_STUDY_CONTENT, Arrays.asList("스프링", "JPA"), TEST_STUDY_ONLINE, false, TEST_STUDY_OPEN, TEST_AREA_CODE, TEST_STUDY_IMAGE_URL, TEST_CATEGORY.getId());

        given(studyRepository.findWithTagById(any()))
                .willReturn(Optional.of(study));

        given(categoryRepository.findById(any()))
                .willReturn(Optional.of(TEST_CATEGORY));

        // when
        studyService.update(TEST_USER_RESPONSE.getUserId(), TEST_STUDY.getId(), studyUpdateRequest);

        // then
        then(studyRepository).should(times(1)).findWithTagById(any());
        then(categoryRepository).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("스터디를 수정할 때 스터디가 존재하지 않으면 예외가 발생한다.")
    void updateNotFound() {
        // given
        given(studyRepository.findWithTagById(any()))
                .willReturn(Optional.empty());

        // when
        assertThrows(NotFoundException.class, () -> studyService.update(TEST_USER_RESPONSE.getUserId(), TEST_STUDY.getId(), TEST_STUDY_UPDATE_REQUEST));

        // then
        then(studyRepository).should(times(1)).findWithTagById(any());
    }

    @Test
    @DisplayName("스터디를 수정할 때 스터디 관리자가 아니면 예외가 발생한다.")
    void updateNotAdmin() {
        // given
        Study study = Study.createStudy(TEST_STUDY_NAME, TEST_STUDY_CONTENT, TEST_STUDY_NUMBER_OF_PEOPLE, TEST_STUDY_ONLINE, TEST_STUDY_OFFLINE, TEST_STUDY_IMAGE_URL, TEST_CHILD_CATEGORY);
        study.addStudyUser(TEST_USER_RESPONSE.getUserId(), StudyRole.ADMIN);

        given(studyRepository.findWithTagById(any()))
                .willReturn(Optional.of(study));

        // when
        assertThrows(AccessDeniedException.class, () -> studyService.update(2L, TEST_STUDY.getId(), TEST_STUDY_UPDATE_REQUEST));

        // then
        then(studyRepository).should(times(1)).findWithTagById(any());
    }

    @Test
    @DisplayName("스터디를 수정할 때 카테고리가 존재하지 않으면 예외가 발생한다.")
    void updateCategoryNotFound() {
        // given
        Study study = Study.createStudy(TEST_STUDY_NAME, TEST_STUDY_CONTENT, TEST_STUDY_NUMBER_OF_PEOPLE, TEST_STUDY_ONLINE, TEST_STUDY_OFFLINE, TEST_STUDY_IMAGE_URL, TEST_CHILD_CATEGORY);
        study.addStudyUser(1L, StudyRole.ADMIN);

        given(studyRepository.findWithTagById(any()))
                .willReturn(Optional.of(study));

        given(categoryRepository.findById(any()))
                .willReturn(Optional.empty());
        // when
        assertThrows(NotFoundException.class, () -> studyService.update(TEST_USER_RESPONSE.getUserId(), TEST_STUDY.getId(), TEST_STUDY_UPDATE_REQUEST));

        // then
        then(studyRepository).should(times(1)).findWithTagById(any());
        then(categoryRepository).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("스터디를 삭제한다.")
    void delete() {
        // given
        Study study = Study.createStudy(TEST_STUDY_NAME, TEST_STUDY_CONTENT, TEST_STUDY_NUMBER_OF_PEOPLE, TEST_STUDY_ONLINE, TEST_STUDY_OFFLINE, TEST_STUDY_IMAGE_URL, TEST_CHILD_CATEGORY);
        study.addStudyUser(TEST_USER_RESPONSE.getUserId(), StudyRole.ADMIN);

        given(studyRepository.findWithStudyUserById(any()))
                .willReturn(Optional.of(study));

        willDoNothing()
                .given(studyRepository)
                .delete(any());

        willDoNothing()
                .given(studyDeleteMessageSender)
                .send(any());

        // when
        studyService.delete(TEST_USER_RESPONSE.getUserId(), TEST_STUDY.getId());

        // then
        then(studyRepository).should(times(1)).findWithStudyUserById(any());
        then(studyRepository).should(times(1)).delete(any());
        then(studyDeleteMessageSender).should(times(1)).send(any());
    }

    @Test
    @DisplayName("스터디를 삭제할때 스터디가 존재하지 않으면 예외가 발생한다.")
    void deleteNotFound() {
        // given
        given(studyRepository.findWithStudyUserById(any()))
                .willReturn(Optional.empty());

        // when
        assertThrows(NotFoundException.class, () -> studyService.delete(TEST_USER_RESPONSE.getUserId(), TEST_STUDY.getId()));

        // then
        then(studyRepository).should(times(1)).findWithStudyUserById(any());
    }

    @Test
    @DisplayName("스터디를 삭제할때 스터디 관리자가 아니면 예외가 발생한다.")
    void deleteNotAdmin() {
        // given
        Study study = study = Study.createStudy(TEST_STUDY_NAME, TEST_STUDY_CONTENT, TEST_STUDY_NUMBER_OF_PEOPLE, TEST_STUDY_ONLINE, TEST_STUDY_OFFLINE, TEST_STUDY_IMAGE_URL, TEST_CHILD_CATEGORY);
        study.addStudyUser(TEST_USER_RESPONSE.getUserId(), StudyRole.ADMIN);

        given(studyRepository.findWithStudyUserById(any()))
                .willReturn(Optional.of(study));

        // when
        assertThrows(AccessDeniedException.class, () -> studyService.delete(2L, TEST_STUDY.getId()));

        // then
        then(studyRepository).should(times(1)).findWithStudyUserById(any());
    }

    @Test
    @DisplayName("스터디를 상세 조회한다.")
    void findById() {
        // given
        Study study = study = Study.createStudy(TEST_STUDY_NAME, TEST_STUDY_CONTENT, TEST_STUDY_NUMBER_OF_PEOPLE, TEST_STUDY_ONLINE, TEST_STUDY_OFFLINE, TEST_STUDY_IMAGE_URL, TEST_CHILD_CATEGORY);
        study.changeArea(TEST_AREA_RESPONSE.getId());
        study.changeTags(Arrays.asList("스프링", "JPA"));

        given(studyRepository.findWithCategoryAndTagById(any()))
                .willReturn(Optional.of(study));

        given(areaServiceClient.findById(any()))
                .willReturn(TEST_AREA_RESPONSE);

        // when
        StudyResponse result = studyService.findById(TEST_STUDY.getId());

        // then
        assertThat(result.getParentCategory().getCategoryId()).isEqualTo(TEST_CHILD_CATEGORY.getId());
        assertThat(result.getParentCategory().getName()).isEqualTo(TEST_CATEGORY.getName());
        assertThat(result.getChildCategory().getCategoryId()).isEqualTo(TEST_CHILD_CATEGORY.getId());
        assertThat(result.getChildCategory().getName()).isEqualTo(TEST_CHILD_CATEGORY.getName());
        assertThat(result.getArea()).isEqualTo(TEST_AREA_RESPONSE);
        assertThat(result.getTags().size()).isEqualTo(2);
        then(studyRepository).should(times(1)).findWithCategoryAndTagById(any());
        then(areaServiceClient).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("스터디를 상세 조회할때 스터디가 존재하지 않으면 예외가 발생한다.")
    void findByIdNotFound() {
        // given
        given(studyRepository.findWithCategoryAndTagById(any()))
                .willReturn(Optional.empty());

        // when
        assertThrows(NotFoundException.class, () -> studyService.findById(TEST_STUDY.getId()));

        // then
        then(studyRepository).should(times(1)).findWithCategoryAndTagById(any());
    }

    @Test
    @DisplayName("온라인 스터디를 상세 조회한다.")
    void findByIdOnline() {
        // given
        Study study = Study.createStudy(TEST_STUDY_NAME, TEST_STUDY_CONTENT, TEST_STUDY_NUMBER_OF_PEOPLE, TEST_STUDY_ONLINE, false, TEST_STUDY_IMAGE_URL, TEST_CHILD_CATEGORY);
        study.changeTags(Arrays.asList("스프링", "JPA"));

        given(studyRepository.findWithCategoryAndTagById(any()))
                .willReturn(Optional.of(study));

        // when
        StudyResponse result = studyService.findById(TEST_STUDY.getId());

        // then
        assertThat(result.getParentCategory().getCategoryId()).isEqualTo(TEST_CHILD_CATEGORY.getId());
        assertThat(result.getParentCategory().getName()).isEqualTo(TEST_CATEGORY.getName());
        assertThat(result.getChildCategory().getCategoryId()).isEqualTo(TEST_CHILD_CATEGORY.getId());
        assertThat(result.getChildCategory().getName()).isEqualTo(TEST_CHILD_CATEGORY.getName());
        assertThat(result.getArea()).isNull();
        assertThat(result.getTags().size()).isEqualTo(2);
        then(studyRepository).should(times(1)).findWithCategoryAndTagById(any());
    }

    @Test
    @DisplayName("비회원이 온라인 스터디를 검색한다.")
    void searchOnlineNotLoginUser() {
        // given
        StudySearchRequest studySearchRequest
                = new StudySearchRequest(TEST_STUDY_NAME, TEST_CATEGORY.getId(), TEST_STUDY_ONLINE, false, TEST_SIZE, TEST_STUDY.getId());

        given(studyQueryRepository.findBySearchCondition(any(), any()))
                .willReturn(Arrays.asList(TEST_STUDY_RESPONSE3));

        // when
        List<StudyResponse> result = studyService.search(null, studySearchRequest);

        // then
        assertThat(result.size()).isEqualTo(1);
        then(studyQueryRepository).should(times(1)).findBySearchCondition(any(), any());
    }

    @Test
    @DisplayName("비회원이 온라인 스터디를 검색한다.")
    void searchOfflineNotLoginUser() {
        // given
        given(studyQueryRepository.findBySearchCondition(any(), any()))
                .willReturn(Arrays.asList(TEST_STUDY_RESPONSE3));

        // when
        List<StudyResponse> result = studyService.search(null, TEST_STUDY_SEARCH_REQUEST);

        // then
        assertThat(result.size()).isEqualTo(1);
        then(studyQueryRepository).should(times(1)).findBySearchCondition(any(), any());
    }

    @Test
    @DisplayName("회원이 온라인 스터디를 조회한다.")
    void searchOnline() {
        // given
        StudySearchRequest studySearchRequest
                = new StudySearchRequest(TEST_STUDY_NAME, TEST_CATEGORY.getId(), TEST_STUDY_ONLINE, false, TEST_SIZE, TEST_STUDY.getId());

        given(studyQueryRepository.findBySearchCondition(any(), any()))
                .willReturn(Arrays.asList(TEST_STUDY_RESPONSE3));

        // when
        List<StudyResponse> result = studyService.search(TEST_USER_RESPONSE.getUserId(), studySearchRequest);

        // then
        assertThat(result.size()).isEqualTo(1);
        then(studyQueryRepository).should(times(1)).findBySearchCondition(any(), any());
    }

    @Test
    @DisplayName("지역을 등록한 회원이 오프라인 스터디를 검색한다.")
    void searchOffline() {
        // given
        given(userServiceClient.findById(any()))
                .willReturn(TEST_USER_RESPONSE);

        given(areaServiceClient.findAroundById(any(), any()))
                .willReturn(Arrays.asList(TEST_AREA_RESPONSE));

        given(studyQueryRepository.findBySearchCondition(any(), any()))
                .willReturn(Arrays.asList(TEST_STUDY_RESPONSE3));

        // when
        List<StudyResponse> result = studyService.search(TEST_USER_RESPONSE.getUserId(), TEST_STUDY_SEARCH_REQUEST);

        // then
        assertThat(result.size()).isEqualTo(1);
        then(userServiceClient).should(times(1)).findById(any());
        then(areaServiceClient).should(times(1)).findAroundById(any(), any());
        then(studyQueryRepository).should(times(1)).findBySearchCondition(any(), any());
    }


    @Test
    @DisplayName("회원의 지역이 없는 오프라인 스터디를 조회한다.")
    void searchNotExistUserArea() {
        // given
        UserResponse userResponse = new UserResponse(1L, TEST_USER_NICKNAME, TEST_USER_AGE_RANGE, TEST_USER_GENDER, TEST_USER_IMAGE_URL, null, TEST_USER_DISTANCE, TEST_FCM_TOKEN);
        given(userServiceClient.findById(any()))
                .willReturn(userResponse);

        given(studyQueryRepository.findBySearchCondition(any(), any()))
                .willReturn(Arrays.asList(TEST_STUDY_RESPONSE3));

        // when
        List<StudyResponse> result = studyService.search(TEST_USER_RESPONSE.getUserId(), TEST_STUDY_SEARCH_REQUEST);

        // then
        assertThat(result.size()).isEqualTo(1);
        then(userServiceClient).should(times(1)).findById(any());
        then(studyQueryRepository).should(times(1)).findBySearchCondition(any(), any());
    }

    @Test
    @DisplayName("스터디 참가 대기자를 추가한다.")
    void createWaitUser() {
        // given
        given(studyRepository.findWithWaitUserById(any()))
                .willReturn(Optional.of(TEST_STUDY));

        // when
        studyService.createWaitUser(TEST_USER_RESPONSE.getUserId(), TEST_STUDY.getId());

        // then
        then(studyRepository).should(times(1)).findWithWaitUserById(any());
    }

    @Test
    @DisplayName("스터디 참가 대기자를 추가할 때 스터디가 존재하지 않으면 예외가 발생한다.")
    void createWaitUserNotFound() {
        // given
        given(studyRepository.findWithWaitUserById(any()))
                .willReturn(Optional.empty());

        // when
        assertThrows(NotFoundException.class, () -> studyService.createWaitUser(TEST_USER_RESPONSE.getUserId(), TEST_STUDY.getId()));

        // then
        then(studyRepository).should(times(1)).findWithWaitUserById(any());
    }

    @Test
    @DisplayName("스터디 참가 대기자를 삭제한다.")
    void deleteWaitUser() {
        // given
        Study study = Study.createStudy(TEST_STUDY_NAME, TEST_STUDY_CONTENT, TEST_STUDY_NUMBER_OF_PEOPLE, TEST_STUDY_ONLINE, TEST_STUDY_OFFLINE, TEST_STUDY_IMAGE_URL, TEST_CHILD_CATEGORY);
        study.addWaitUser(1L);

        given(studyRepository.findWithWaitUserById(any()))
                .willReturn(Optional.of(study));

        // when
        studyService.deleteWaitUser(TEST_USER_RESPONSE.getUserId(), TEST_STUDY.getId());

        // then
        then(studyRepository).should(times(1)).findWithWaitUserById(any());
    }

    @Test
    @DisplayName("스터디 참가 대기자를 삭제할 때 스터디가 존재하지 않으면 예외가 발생한다.")
    void deleteWaitUserNotFound() {
        // given
        given(studyRepository.findWithWaitUserById(any()))
                .willReturn(Optional.empty());

        // when
        assertThrows(NotFoundException.class, () -> studyService.deleteWaitUser(TEST_USER_RESPONSE.getUserId(), TEST_STUDY.getId()));

        // then
        then(studyRepository).should(times(1)).findWithWaitUserById(any());
    }

    @Test
    @DisplayName("스터디 참가 대기자를 거부한다.")
    void failWaitUser() {
        // given
        Study study = Study.createStudy(TEST_STUDY_NAME, TEST_STUDY_CONTENT, TEST_STUDY_NUMBER_OF_PEOPLE, TEST_STUDY_ONLINE, TEST_STUDY_OFFLINE, TEST_STUDY_IMAGE_URL, TEST_CHILD_CATEGORY);
        study.addStudyUser(TEST_USER_RESPONSE.getUserId(), StudyRole.ADMIN);
        study.addWaitUser(2L);

        given(studyRepository.findWithWaitUserById(any()))
                .willReturn(Optional.of(study));

        willDoNothing()
                .given(studyApplyFailMessageSender)
                .send(any());

        // when
        studyService.failWaitUser(TEST_USER_RESPONSE.getUserId(), TEST_STUDY.getId(), 2L);

        // then
        then(studyRepository).should(times(1)).findWithWaitUserById(any());
        then(studyApplyFailMessageSender).should(times(1)).send(any());
    }

    @Test
    @DisplayName("스터디 참가 대기자를 거부할 때 스터디가 존재하지 않으면 예외가 발생한다.")
    void failWaitUserNotFound() {
        // given
        given(studyRepository.findWithWaitUserById(any()))
                .willReturn(Optional.empty());

        // when
        assertThrows(NotFoundException.class, () -> studyService.failWaitUser(TEST_USER_RESPONSE.getUserId(), TEST_STUDY.getId(), 2L));

        // then
        then(studyRepository).should(times(1)).findWithWaitUserById(any());
    }

    @Test
    @DisplayName("스터디 참가 대기자를 거부할 때 스터디 관리자가 아니면 예외가 발생한다.")
    void failWaitUserNotAdmin() {
        // given
        Study study = Study.createStudy(TEST_STUDY_NAME, TEST_STUDY_CONTENT, TEST_STUDY_NUMBER_OF_PEOPLE, TEST_STUDY_ONLINE, TEST_STUDY_OFFLINE, TEST_STUDY_IMAGE_URL, TEST_CHILD_CATEGORY);
        study.addStudyUser(TEST_USER_RESPONSE.getUserId(), StudyRole.ADMIN);
        study.addWaitUser(2L);

        given(studyRepository.findWithWaitUserById(any()))
                .willReturn(Optional.of(study));

        // when
        assertThrows(AccessDeniedException.class, () -> studyService.failWaitUser(2L, TEST_STUDY.getId(), 2L));

        // then
        then(studyRepository).should(times(1)).findWithWaitUserById(any());
    }

    @Test
    @DisplayName("스터디 참가 대기자 리스트를 조회한다.")
    void findWaitUsersById() {
        // given
        Study study = Study.createStudy(TEST_STUDY_NAME, TEST_STUDY_CONTENT, TEST_STUDY_NUMBER_OF_PEOPLE, TEST_STUDY_ONLINE, TEST_STUDY_OFFLINE, TEST_STUDY_IMAGE_URL, TEST_CHILD_CATEGORY);
        study.addWaitUser(1L);

        given(studyRepository.findWithWaitUserById(any()))
                .willReturn(Optional.of(study));

        given(userServiceClient.findByIdIn(any()))
                .willReturn(Arrays.asList(TEST_USER_RESPONSE));

        // when
        List<UserResponse> result = studyService.findWaitUsersById(TEST_STUDY.getId());

        // then
        assertThat(result.size()).isEqualTo(1);
        then(studyRepository).should(times(1)).findWithWaitUserById(any());
        then(userServiceClient).should(times(1)).findByIdIn(any());
    }

    @Test
    @DisplayName("스터디 참가 대기자 리스트를 조회할 때 스터디가 존재하지 않으면 예외가 발생한다.")
    void findWaitUsersByIdNotFound() {
        // given
        given(studyRepository.findWithWaitUserById(any()))
                .willReturn(Optional.empty());

        // when
        assertThrows(NotFoundException.class, () -> studyService.findWaitUsersById(TEST_STUDY.getId()));

        // then
        then(studyRepository).should(times(1)).findWithWaitUserById(any());
    }

    @Test
    @DisplayName("스터디 참가자를 추가한다.")
    void createStudyUser() {
        // given
        Study study = Study.createStudy(TEST_STUDY_NAME, TEST_STUDY_CONTENT, TEST_STUDY_NUMBER_OF_PEOPLE, TEST_STUDY_ONLINE, TEST_STUDY_OFFLINE, TEST_STUDY_IMAGE_URL, TEST_CHILD_CATEGORY);
        study.addStudyUser(TEST_USER_RESPONSE.getUserId(), StudyRole.ADMIN);
        study.addWaitUser(2L);

        given(studyRepository.findWithWaitUserById(any()))
                .willReturn(Optional.of(study));

        willDoNothing()
                .given(studyApplySuccessMessageSender)
                .send(any());
        // when
        studyService.createStudyUser(TEST_USER_RESPONSE.getUserId(), TEST_STUDY.getId(), 2L);

        // then
        then(studyRepository).should(times(1)).findWithWaitUserById(any());
        then(studyApplySuccessMessageSender).should(times(1)).send(any());
    }

    @Test
    @DisplayName("스터디 참가자를 추가할 때 스터디가 존재하지 않으면 예외가 발생한다.")
    void createStudyUserNotFound() {
        // given
        given(studyRepository.findWithWaitUserById(any()))
                .willReturn(Optional.empty());

        // when
        assertThrows(NotFoundException.class, () -> studyService.createStudyUser(TEST_USER_RESPONSE.getUserId(), TEST_STUDY.getId(), 2L));

        // then
        then(studyRepository).should(times(1)).findWithWaitUserById(any());
    }

    @Test
    @DisplayName("스터디 참가자를 추가할 때 스터디 관리자가 아니면 예외가 발생한다.")
    void createStudyUserNotAdmin() {
        // given
        Study study = Study.createStudy(TEST_STUDY_NAME, TEST_STUDY_CONTENT, TEST_STUDY_NUMBER_OF_PEOPLE, TEST_STUDY_ONLINE, TEST_STUDY_OFFLINE, TEST_STUDY_IMAGE_URL, TEST_CHILD_CATEGORY);
        study.addStudyUser(TEST_USER_RESPONSE.getUserId(), StudyRole.ADMIN);
        study.addWaitUser(2L);

        given(studyRepository.findWithWaitUserById(any()))
                .willReturn(Optional.of(study));

        // when
        assertThrows(AccessDeniedException.class, () -> studyService.createStudyUser(2L, TEST_STUDY.getId(), 2L));

        // then
        then(studyRepository).should(times(1)).findWithWaitUserById(any());
    }

    @Test
    @DisplayName("스터디 관리자가 스터디 참가자를 삭제한다.")
    void deleteStudyUser() {
        // given
        Study study = Study.createStudy(TEST_STUDY_NAME, TEST_STUDY_CONTENT, TEST_STUDY_NUMBER_OF_PEOPLE, TEST_STUDY_ONLINE, TEST_STUDY_OFFLINE, TEST_STUDY_IMAGE_URL, TEST_CHILD_CATEGORY);
        study.addStudyUser(TEST_USER_RESPONSE.getUserId(), StudyRole.ADMIN);
        study.addStudyUser(2L, StudyRole.USER);

        given(studyRepository.findWithStudyUserById(any()))
                .willReturn(Optional.of(study));

        // when
        studyService.deleteStudyUser(TEST_USER_RESPONSE.getUserId(), TEST_STUDY.getId(), 2L);

        // then
        then(studyRepository).should(times(1)).findWithStudyUserById(any());
    }

    @Test
    @DisplayName("스터디 관리자가 스터디 참가자를 삭제할 때 스터디가 존재하지 않으면 예외가 발생한다.")
    void deleteStudyUserNotFound() {
        // given
        given(studyRepository.findWithStudyUserById(any()))
                .willReturn(Optional.empty());

        // when
        assertThrows(NotFoundException.class, () -> studyService.deleteStudyUser(TEST_USER_RESPONSE.getUserId(), TEST_STUDY.getId(), 2L));

        // then
        then(studyRepository).should(times(1)).findWithStudyUserById(any());
    }

    @Test
    @DisplayName("스터디 관리자가 스터디 참가자를 삭제할 때 스터디 관리자가 아니면 예외가 발생한다.")
    void deleteStudyUserNotAdmin() {
        // given
        Study study = Study.createStudy(TEST_STUDY_NAME, TEST_STUDY_CONTENT, TEST_STUDY_NUMBER_OF_PEOPLE, TEST_STUDY_ONLINE, TEST_STUDY_OFFLINE, TEST_STUDY_IMAGE_URL, TEST_CHILD_CATEGORY);
        study.addStudyUser(TEST_USER_RESPONSE.getUserId(), StudyRole.ADMIN);
        study.addStudyUser(2L, StudyRole.USER);

        given(studyRepository.findWithStudyUserById(any()))
                .willReturn(Optional.of(study));

        // when
        assertThrows(AccessDeniedException.class, () -> studyService.deleteStudyUser(2L, TEST_STUDY.getId(), 2L));

        // then
        then(studyRepository).should(times(1)).findWithStudyUserById(any());
    }


    @Test
    @DisplayName("스터디 참가자가 탈퇴한다.")
    void deleteStudyUserSelf() {
        // given
        Study study = Study.createStudy(TEST_STUDY_NAME, TEST_STUDY_CONTENT, TEST_STUDY_NUMBER_OF_PEOPLE, TEST_STUDY_ONLINE, TEST_STUDY_OFFLINE, TEST_STUDY_IMAGE_URL, TEST_CHILD_CATEGORY);
        study.addStudyUser(TEST_USER_RESPONSE.getUserId(), StudyRole.ADMIN);
        study.addStudyUser(2L, StudyRole.USER);

        given(studyRepository.findWithStudyUserById(any()))
                .willReturn(Optional.of(study));
        // when
        studyService.deleteStudyUser(2L, TEST_STUDY.getId());

        // then
        then(studyRepository).should(times(1)).findWithStudyUserById(any());
    }

    @Test
    @DisplayName("스터디 참가자가 탈퇴할 때 스터디가 존재하지 않으면 예외가 발생한다.")
    void deleteStudyUserSelfNotFound() {
        // given
        given(studyRepository.findWithStudyUserById(any()))
                .willReturn(Optional.empty());

        // when
        assertThrows(NotFoundException.class, () -> studyService.deleteStudyUser(2L, TEST_STUDY.getId()));

        // then
        then(studyRepository).should(times(1)).findWithStudyUserById(any());
    }

    @Test
    @DisplayName("스터디 참가자 리스트를 조회한다.")
    void findStudyUsersById() {
        // given
        Study study = Study.createStudy(TEST_STUDY_NAME, TEST_STUDY_CONTENT, TEST_STUDY_NUMBER_OF_PEOPLE, TEST_STUDY_ONLINE, TEST_STUDY_OFFLINE, TEST_STUDY_IMAGE_URL, TEST_CHILD_CATEGORY);
        study.addStudyUser(1L, StudyRole.ADMIN);

        given(studyRepository.findWithStudyUserById(any()))
                .willReturn(Optional.of(study));

        given(userServiceClient.findByIdIn(any()))
                .willReturn(Arrays.asList(TEST_USER_RESPONSE));

        // when
        List<StudyUserResponse> result = studyService.findStudyUsersById(TEST_STUDY.getId());

        // then
        assertThat(result.size()).isEqualTo(1);
        then(studyRepository).should(times(1)).findWithStudyUserById(any());
        then(userServiceClient).should(times(1)).findByIdIn(any());
    }

    @Test
    @DisplayName("스터디 참가자 리스트를 조회할 때 스터디가 존재하지 않으면 예외가 발생한다.")
    void findStudyUsersByIdNotFound() {
        // given
        given(studyRepository.findWithStudyUserById(any()))
                .willReturn(Optional.empty());

        // when
        assertThrows(NotFoundException.class, () -> studyService.findStudyUsersById(TEST_STUDY.getId()));

        // then
        then(studyRepository).should(times(1)).findWithStudyUserById(any());
    }

    @Test
    @DisplayName("스터디 채팅방을 생성한다.")
    void createChatRoom() {
        // given
        Study study = Study.createStudy(TEST_STUDY_NAME, TEST_STUDY_CONTENT, TEST_STUDY_NUMBER_OF_PEOPLE, TEST_STUDY_ONLINE, TEST_STUDY_OFFLINE, TEST_STUDY_IMAGE_URL, TEST_CHILD_CATEGORY);
        study.addStudyUser(TEST_USER_RESPONSE.getUserId(), StudyRole.ADMIN);

        given(studyRepository.findWithChatRoomById(any()))
                .willReturn(Optional.of(study));

        // when
        studyService.createChatRoom(TEST_USER_RESPONSE.getUserId(), TEST_STUDY.getId(), TEST_CHAT_ROOM_CREATE_REQUEST);

        // then
        then(studyRepository).should(times(1)).findWithChatRoomById(any());
    }

    @Test
    @DisplayName("스터디 채팅방을 생성할 때 스터디 관리자가 아니면 예외가 발생한다.")
    void createChatRoomNotAdmin() {
        // given
        Study study = Study.createStudy(TEST_STUDY_NAME, TEST_STUDY_CONTENT, TEST_STUDY_NUMBER_OF_PEOPLE, TEST_STUDY_ONLINE, TEST_STUDY_OFFLINE, TEST_STUDY_IMAGE_URL, TEST_CHILD_CATEGORY);
        study.addStudyUser(TEST_USER_RESPONSE.getUserId(), StudyRole.ADMIN);

        given(studyRepository.findWithChatRoomById(any()))
                .willReturn(Optional.of(study));

        // when
        assertThrows(AccessDeniedException.class, () -> studyService.createChatRoom(2L, TEST_STUDY.getId(), TEST_CHAT_ROOM_CREATE_REQUEST));

        // then
        then(studyRepository).should(times(1)).findWithChatRoomById(any());
    }

    @Test
    @DisplayName("스터디 채팅방을 생성할 때 스터디가 존재하지 않으면 예외가 발생한다.")
    void createChatRoomNotFound() {
        // given
        given(studyRepository.findWithChatRoomById(any()))
                .willReturn(Optional.empty());

        // when
        assertThrows(NotFoundException.class, () -> studyService.createChatRoom(TEST_USER_RESPONSE.getUserId(), TEST_STUDY.getId(), TEST_CHAT_ROOM_CREATE_REQUEST));

        // then
        then(studyRepository).should(times(1)).findWithChatRoomById(any());
    }

    @Test
    @DisplayName("스터디 채팅방을 수정한다.")
    void updateChatRoom() {
        // given
        Study study = Study.createStudy(TEST_STUDY_NAME, TEST_STUDY_CONTENT, TEST_STUDY_NUMBER_OF_PEOPLE, TEST_STUDY_ONLINE, TEST_STUDY_OFFLINE, TEST_STUDY_IMAGE_URL, TEST_CHILD_CATEGORY);
        study.addStudyUser(TEST_USER_RESPONSE.getUserId(), StudyRole.ADMIN);
        study.getChatRooms().add(new ChatRoom(1L, "떠들기", study));

        given(studyRepository.findWithChatRoomById(any()))
                .willReturn(Optional.of(study));
        // when
        studyService.updateChatRoom(1L, 1L, 1L, TEST_CHAT_ROOM_UPDATE_REQUEST);

        // then
        then(studyRepository).should(times(1)).findWithChatRoomById(any());
    }

    @Test
    @DisplayName("스터디 채팅방을 수정할 때 스터디가 존재하지 않으면 예외가 발생한다.")
    void updateChatRoomNotFound() {
        // given
        given(studyRepository.findWithChatRoomById(any()))
                .willReturn(Optional.empty());
        // when
        assertThrows(NotFoundException.class, () -> studyService.updateChatRoom(1L, TEST_STUDY.getId(), 1L, TEST_CHAT_ROOM_UPDATE_REQUEST));

        // then
        then(studyRepository).should(times(1)).findWithChatRoomById(any());
    }

    @Test
    @DisplayName("스터디 채팅방을 수정할 때 스터디 관리자가 아니면 예외가 발생한다.")
    void updateChatRoomNotAdmin() {
        // given
        Study study = Study.createStudy(TEST_STUDY_NAME, TEST_STUDY_CONTENT, TEST_STUDY_NUMBER_OF_PEOPLE, TEST_STUDY_ONLINE, TEST_STUDY_OFFLINE, TEST_STUDY_IMAGE_URL, TEST_CHILD_CATEGORY);
        study.addStudyUser(TEST_USER_RESPONSE.getUserId(), StudyRole.ADMIN);
        study.getChatRooms().add(new ChatRoom(1L, "떠들기", study));

        given(studyRepository.findWithChatRoomById(any()))
                .willReturn(Optional.of(study));
        // when
        assertThrows(AccessDeniedException.class, () -> studyService.updateChatRoom(2L, TEST_STUDY.getId(), 1L, TEST_CHAT_ROOM_UPDATE_REQUEST));

        // then
        then(studyRepository).should(times(1)).findWithChatRoomById(any());
    }

    @Test
    @DisplayName("스터디 채팅방을 삭제한다.")
    void deleteChatRoom() {
        // given
        Study study = Study.createStudy(TEST_STUDY_NAME, TEST_STUDY_CONTENT, TEST_STUDY_NUMBER_OF_PEOPLE, TEST_STUDY_ONLINE, TEST_STUDY_OFFLINE, TEST_STUDY_IMAGE_URL, TEST_CHILD_CATEGORY);
        study.addStudyUser(TEST_USER_RESPONSE.getUserId(), StudyRole.ADMIN);
        study.getChatRooms().add(new ChatRoom(1L, TEST_CHAT_ROOM_NAME, study));

        given(studyRepository.findWithChatRoomById(any()))
                .willReturn(Optional.of(study));

        willDoNothing()
                .given(chatRoomDeleteMessageSender)
                .send(any());

        // when
        studyService.deleteChatRoom(1L, 1L, 1L);

        // then
        then(studyRepository).should(times(1)).findWithChatRoomById(any());
        then(chatRoomDeleteMessageSender).should(times(1)).send(any());
    }


    @Test
    @DisplayName("스터디 채팅방을 삭제할 때 스터디가 존재하지 않으면 예외가 발생한다.")
    void deleteChatRoomNotFound() {
        // given
        given(studyRepository.findWithChatRoomById(any()))
                .willReturn(Optional.empty());

        // when
        assertThrows(NotFoundException.class, () -> studyService.deleteChatRoom(1L, TEST_STUDY.getId(), 1L));

        // then
        then(studyRepository).should(times(1)).findWithChatRoomById(any());
    }

    @Test
    @DisplayName("스터디 채팅방을 삭제할 때 스터디 관리자가 아니면 예외가 발생한다.")
    void deleteChatRoomNotAdmin() {
        // given
        Study study = Study.createStudy(TEST_STUDY_NAME, TEST_STUDY_CONTENT, TEST_STUDY_NUMBER_OF_PEOPLE, TEST_STUDY_ONLINE, TEST_STUDY_OFFLINE, TEST_STUDY_IMAGE_URL, TEST_CHILD_CATEGORY);
        study.addStudyUser(TEST_USER_RESPONSE.getUserId(), StudyRole.ADMIN);
        study.getChatRooms().add(new ChatRoom(1L, TEST_CHAT_ROOM_NAME, study));

        given(studyRepository.findWithChatRoomById(any()))
                .willReturn(Optional.of(study));

        // when
        assertThrows(AccessDeniedException.class, () -> studyService.deleteChatRoom(2L, TEST_STUDY.getId(), 1L));

        // then
        then(studyRepository).should(times(1)).findWithChatRoomById(any());
    }

    @Test
    @DisplayName("스터디 채팅방을 조회한다.")
    void findChatRoomsById() {
        // given
        Study study = Study.createStudy(TEST_STUDY_NAME, TEST_STUDY_CONTENT, TEST_STUDY_NUMBER_OF_PEOPLE, TEST_STUDY_ONLINE, TEST_STUDY_OFFLINE, TEST_STUDY_IMAGE_URL, TEST_CHILD_CATEGORY);
        study.getChatRooms().add(new ChatRoom(1L, TEST_CHAT_ROOM_NAME, study));

        given(studyRepository.findWithChatRoomById(any()))
                .willReturn(Optional.of(study));

        // when
        List<ChatRoomResponse> result = studyService.findChatRoomsById(TEST_STUDY.getId());

        // then
        assertThat(result.size()).isEqualTo(1);
        then(studyRepository).should(times(1)).findWithChatRoomById(any());
    }

    @Test
    @DisplayName("스터디 채팅방을 조회할때 스터디가 존재하지 않으면 예외가 발생한다.")
    void findChatRoomsByIdNotFOund() {
        // given
        given(studyRepository.findWithChatRoomById(any()))
                .willReturn(Optional.empty());

        // when
        assertThrows(NotFoundException.class, () -> studyService.findChatRoomsById(TEST_STUDY.getId()));

        // then
        then(studyRepository).should(times(1)).findWithChatRoomById(any());
    }

    @Test
    @DisplayName("회원이 가입된 스터디를 조회한다.")
    void findByUserId() {
        // given
        given(studyQueryRepository.findByStudyUserId(any()))
                .willReturn(Arrays.asList(TEST_STUDY_RESPONSE3));

        // when
        List<StudyResponse> result = studyService.findByUserId(TEST_USER_RESPONSE.getUserId());

        // then
        assertThat(result.size()).isEqualTo(1);
        then(studyQueryRepository).should(times(1)).findByStudyUserId(any());
    }

    @Test
    @DisplayName("회원이 가입 신청한 스터디를 조회한다.")
    void findByWaitUserId() {
        // given
        given(studyQueryRepository.findByWaitUserId(any()))
                .willReturn(Arrays.asList(TEST_STUDY_RESPONSE3));

        // when
        List<StudyResponse> result = studyService.findByWaitUserId(1L);

        // then
        assertThat(result.size()).isEqualTo(1L);
        then(studyQueryRepository).should(times(1)).findByWaitUserId(any());
    }

    @Test
    @DisplayName("채팅방 ID 로 스터디를 조회한다.")
    void findByChatRoomId() {
        // given
        Study study = Study.createStudy(TEST_STUDY_NAME, TEST_STUDY_CONTENT, TEST_STUDY_NUMBER_OF_PEOPLE, TEST_STUDY_ONLINE, TEST_STUDY_OFFLINE, TEST_STUDY_IMAGE_URL, TEST_CHILD_CATEGORY);
        study.getChatRooms().add(new ChatRoom(1L, TEST_CHAT_ROOM_NAME, study));

        given(studyRepository.findByChatRoomId(any()))
                .willReturn(Optional.of(study));
        // when
        StudyResponse result = studyService.findByChatRoomId(1L);

        // then
        assertThat(result.getName()).isEqualTo(study.getName());
        then(studyRepository).should(times(1)).findByChatRoomId(any());
    }

    @Test
    @DisplayName("채팅방 ID 로 스터디를 조회할 때 스터디가 존재하지 않으면 예외가 발생한다.")
    void findByChatRoomIdNotFound() {
        // given
        given(studyRepository.findByChatRoomId(any()))
                .willReturn(Optional.empty());
        // when
        assertThrows(NotFoundException.class, () -> studyService.findByChatRoomId(1L));

        // then
        then(studyRepository).should(times(1)).findByChatRoomId(any());
    }

    @Test
    @DisplayName("채팅방을 단건 조회한다.")
    void findByChatRoomByIdAndChatRoomId() {
        // given
        Study study = Study.createStudy(TEST_STUDY_NAME, TEST_STUDY_CONTENT, TEST_STUDY_NUMBER_OF_PEOPLE, TEST_STUDY_ONLINE, TEST_STUDY_OFFLINE, TEST_STUDY_IMAGE_URL, TEST_CHILD_CATEGORY);
        study.getChatRooms().add(new ChatRoom(1L, TEST_CHAT_ROOM_NAME, study));

        given(studyRepository.findWithChatRoomById(any()))
                .willReturn(Optional.of(study));

        // when
        ChatRoomResponse result = studyService.findChatRoomByIdAndChatRoomId(TEST_STUDY.getId(), 1L);

        // then
        assertThat(result.getChatRoomId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo(TEST_CHAT_ROOM_NAME);
        then(studyRepository).should(times(1)).findWithChatRoomById(any());
    }

    @Test
    @DisplayName("채팅방을 단건 조회할때 스터디가 존재하지 않으면 예외가 발생한다.")
    void findByChatRoomByIdAndChatRoomIdNotFound() {
        // given
        given(studyRepository.findWithChatRoomById(any()))
                .willReturn(Optional.empty());

        // when
        assertThrows(NotFoundException.class, () -> studyService.findChatRoomByIdAndChatRoomId(TEST_STUDY.getId(), 1L));

        // then
        then(studyRepository).should(times(1)).findWithChatRoomById(any());
    }

    @Test
    @DisplayName("이미지를 업로드한다.")
    void uploadImage() {
        // given
        given(awsClient.upload(any()))
                .willReturn(TEST_STUDY_IMAGE_URL);
        // when
        String result = studyService.uploadImage(TEST_IMAGE_FILE);

        // then
        assertThat(result).isEqualTo(TEST_STUDY_IMAGE_URL);
        then(awsClient).should(times(1)).upload(any());

    }
}