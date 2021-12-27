package com.study.service;

import com.study.client.AreaServiceClient;
import com.study.client.UserResponse;
import com.study.client.UserServiceClient;
import com.study.domain.*;
import com.study.dto.chatroom.ChatRoomResponse;
import com.study.dto.study.StudyCreateRequest;
import com.study.dto.study.StudyResponse;
import com.study.dto.study.StudySearchRequest;
import com.study.dto.studyuser.StudyUserResponse;
import com.study.kakfa.UserDeleteMessage;
import com.study.kakfa.sender.StudyApplyFailMessageSender;
import com.study.kakfa.sender.StudyApplySuccessMessageSender;
import com.study.kakfa.sender.StudyCreateMessageSender;
import com.study.kakfa.sender.StudyDeleteMessageSender;
import com.study.repository.CategoryRepository;
import com.study.repository.StudyQueryRepository;
import com.study.repository.StudyRepository;
import com.study.util.ImageUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.study.fixture.CategoryFixture.TEST_CATEGORY;
import static com.study.fixture.CategoryFixture.TEST_CHILD_CATEGORY;
import static com.study.fixture.StudyFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
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
    private ImageUtil imageUtil;

    @Mock
    private StudyCreateMessageSender studyCreateMessageSender;

    @Mock
    private StudyDeleteMessageSender studyDeleteMessageSender;

    @Mock
    private StudyApplyFailMessageSender studyApplyFailMessageSender;

    @Mock
    private StudyApplySuccessMessageSender studyApplySuccessMessageSender;

    @Test
    @DisplayName("온라인/오프라인을 지원하는 스터디를 생성한다.")
    void create() {
        // given
        given(categoryRepository.findWithParentById(any()))
                .willReturn(Optional.of(TEST_CHILD_CATEGORY));

        given(areaServiceClient.findByCode(any()))
                .willReturn(TEST_AREA_RESPONSE);

        given(imageUtil.uploadImage(any(), any()))
                .willReturn(TEST_IMAGE);

        given(studyRepository.save(any()))
                .willReturn(null);

        willDoNothing()
                .given(studyCreateMessageSender)
                .send(any());

        // when
        StudyResponse result = studyService.create(1L, TEST_IMAGE_FILE, TEST_STUDY_CREATE_REQUEST);

        // then
        assertThat(result.getName()).isEqualTo(TEST_STUDY_CREATE_REQUEST.getName());
        assertThat(result.getContent()).isEqualTo(TEST_STUDY_CREATE_REQUEST.getContent());
        assertThat(result.getCurrentNumberOfPeople()).isEqualTo(1);
        assertThat(result.getNumberOfPeople()).isEqualTo(TEST_STUDY_CREATE_REQUEST.getNumberOfPeople());
        assertThat(result.getArea()).isEqualTo(TEST_AREA_RESPONSE);
        assertThat(result.isOnline()).isEqualTo(TEST_STUDY_CREATE_REQUEST.getOnline());
        assertThat(result.isOffline()).isEqualTo(TEST_STUDY_CREATE_REQUEST.getOffline());
        assertThat(result.getStatus()).isEqualTo(StudyStatus.OPEN);
        assertThat(result.getImageUrl()).isEqualTo(TEST_IMAGE.getImageUrl());
        assertThat(result.getParentCategory().getName()).isEqualTo(TEST_CATEGORY.getName());
        assertThat(result.getChildCategory().getName()).isEqualTo(TEST_CHILD_CATEGORY.getName());
        assertThat(result.getTags().size()).isEqualTo(TEST_STUDY_CREATE_REQUEST.getTags().size());
        then(categoryRepository).should(times(1)).findWithParentById(any());
        then(areaServiceClient).should(times(1)).findByCode(any());
        then(imageUtil).should(times(1)).uploadImage(any(), any());
        then(studyRepository).should(times(1)).save(any());
        then(studyCreateMessageSender).should(times(1)).send(any());
    }

    @Test
    @DisplayName("온라인 스터디를 생성한다.")
    void createOnline() {
        // given
        StudyCreateRequest studyCreateRequest
                = new StudyCreateRequest("스프링", 5, "스프링", Arrays.asList("스프링", "JPA"), true, false, null, 1L);

        given(categoryRepository.findWithParentById(any()))
                .willReturn(Optional.of(TEST_CHILD_CATEGORY));

        given(imageUtil.uploadImage(any(), any()))
                .willReturn(TEST_IMAGE);

        given(studyRepository.save(any()))
                .willReturn(null);

        willDoNothing()
                .given(studyCreateMessageSender)
                .send(any());

        // when
        StudyResponse result = studyService.create(1L, TEST_IMAGE_FILE, studyCreateRequest);

        // then
        assertThat(result.getName()).isEqualTo(studyCreateRequest.getName());
        assertThat(result.getContent()).isEqualTo(studyCreateRequest.getContent());
        assertThat(result.getCurrentNumberOfPeople()).isEqualTo(1);
        assertThat(result.getNumberOfPeople()).isEqualTo(studyCreateRequest.getNumberOfPeople());
        assertThat(result.getArea().getId()).isNull();
        assertThat(result.isOnline()).isEqualTo(studyCreateRequest.getOnline());
        assertThat(result.isOffline()).isEqualTo(studyCreateRequest.getOffline());
        assertThat(result.getStatus()).isEqualTo(StudyStatus.OPEN);
        assertThat(result.getImageUrl()).isEqualTo(TEST_IMAGE.getImageUrl());
        assertThat(result.getParentCategory().getName()).isEqualTo(TEST_CATEGORY.getName());
        assertThat(result.getChildCategory().getName()).isEqualTo(TEST_CHILD_CATEGORY.getName());
        assertThat(result.getTags().size()).isEqualTo(studyCreateRequest.getTags().size());
        then(categoryRepository).should(times(1)).findWithParentById(any());
        then(imageUtil).should(times(1)).uploadImage(any(), any());
        then(studyRepository).should(times(1)).save(any());
        then(studyCreateMessageSender).should(times(1)).send(any());
    }

    @Test
    @DisplayName("스터디 이미지를 수정한다.")
    void updateImage() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, true, true, null);
        study.addStudyUser(1L, StudyRole.ADMIN);

        given(studyRepository.findWithStudyUserById(any()))
                .willReturn(Optional.of(study));

        given(imageUtil.uploadImage(any(), any()))
                .willReturn(TEST_IMAGE);

        // when
        StudyResponse result = studyService.updateImage(1L, 1L, TEST_IMAGE_FILE);

        // then
        assertThat(result.getImageUrl()).isEqualTo(TEST_IMAGE.getImageUrl());
        then(studyRepository).should(times(1)).findWithStudyUserById(any());
        then(imageUtil).should(times(1)).uploadImage(any(), any());
    }

    @Test
    @DisplayName("스터디 지역을 수정한다.")
    void updateArea() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, true, true, null);
        study.addStudyUser(1L, StudyRole.ADMIN);

        given(studyRepository.findWithStudyUserById(any()))
                .willReturn(Optional.of(study));

        given(areaServiceClient.findByCode(any()))
                .willReturn(TEST_AREA_RESPONSE);

        // when
        StudyResponse result = studyService.updateArea(1L, 1L, TEST_STUDY_UPDATE_AREA_REQUEST);

        // then
        assertThat(result.getArea()).isEqualTo(TEST_AREA_RESPONSE);
        then(studyRepository).should(times(1)).findWithStudyUserById(any());
        then(areaServiceClient).should(times(1)).findByCode(any());
    }

    @Test
    @DisplayName("스터디를 수정한다.")
    void update() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, true, true, null);
        study.addStudyUser(1L, StudyRole.ADMIN);

        given(studyRepository.findWithStudyUserById(any()))
                .willReturn(Optional.of(study));

        given(categoryRepository.findWithParentById(any()))
                .willReturn(Optional.of(TEST_CHILD_CATEGORY));

        // when
        StudyResponse result = studyService.update(1L, 1L, TEST_STUDY_UPDATE_REQUEST);

        // then
        assertThat(result.getName()).isEqualTo(TEST_STUDY_UPDATE_REQUEST.getName());
        assertThat(result.getContent()).isEqualTo(TEST_STUDY_UPDATE_REQUEST.getContent());
        assertThat(result.getNumberOfPeople()).isEqualTo(TEST_STUDY_UPDATE_REQUEST.getNumberOfPeople());
        assertThat(result.isOnline()).isEqualTo(TEST_STUDY_UPDATE_REQUEST.getOnline());
        assertThat(result.isOffline()).isEqualTo(TEST_STUDY_UPDATE_REQUEST.getOffline());
        assertThat(result.getStatus()).isEqualTo(StudyStatus.CLOSE);
        assertThat(result.getParentCategory().getName()).isEqualTo(TEST_CATEGORY.getName());
        assertThat(result.getChildCategory().getName()).isEqualTo(TEST_CHILD_CATEGORY.getName());
        then(studyRepository).should(times(1)).findWithStudyUserById(any());
        then(categoryRepository).should(times(1)).findWithParentById(any());
    }

    @Test
    @DisplayName("스터디를 삭제한다.")
    void delete() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, true, true, null);
        study.addStudyUser(1L, StudyRole.ADMIN);

        given(studyRepository.findWithStudyUserById(any()))
                .willReturn(Optional.of(study));

        willDoNothing()
                .given(studyRepository)
                .delete(any());

        willDoNothing()
                .given(studyDeleteMessageSender)
                .send(any());

        // when
        studyService.delete(1L, 1L);

        // then
        then(studyRepository).should(times(1)).findWithStudyUserById(any());
        then(studyRepository).should(times(1)).delete(any());
        then(studyDeleteMessageSender).should(times(1)).send(any());
    }

    @Test
    @DisplayName("스터디 ID 로 스터디를 조회한다.")
    void findById() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, true, true, TEST_CHILD_CATEGORY);
        study.changeArea(1L);
        study.addTags(Arrays.asList("스프링", "JPA"));

        given(studyQueryRepository.findWithCategoryAndTagById(any()))
                .willReturn(study);

        given(areaServiceClient.findById(any()))
                .willReturn(TEST_AREA_RESPONSE);

        // when
        StudyResponse result = studyService.findById(1L);

        // then
        assertThat(result.getParentCategory().getName()).isEqualTo(TEST_CATEGORY.getName());
        assertThat(result.getChildCategory().getName()).isEqualTo(TEST_CHILD_CATEGORY.getName());
        assertThat(result.getArea()).isEqualTo(TEST_AREA_RESPONSE);
        assertThat(result.getTags().size()).isEqualTo(2);
        then(studyQueryRepository).should(times(1)).findWithCategoryAndTagById(any());
        then(areaServiceClient).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("스터디 ID 로 온라인 스터디를 조회한다.")
    void findByIdOnline() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, true, false, TEST_CHILD_CATEGORY);
        study.addTags(Arrays.asList("스프링", "JPA"));

        given(studyQueryRepository.findWithCategoryAndTagById(any()))
                .willReturn(study);

        // when
        StudyResponse result = studyService.findById(1L);

        // then
        assertThat(result.getParentCategory().getName()).isEqualTo(TEST_CATEGORY.getName());
        assertThat(result.getChildCategory().getName()).isEqualTo(TEST_CHILD_CATEGORY.getName());
        assertThat(result.getArea()).isNull();
        assertThat(result.getTags().size()).isEqualTo(2);
        then(studyQueryRepository).should(times(1)).findWithCategoryAndTagById(any());
    }

    @Test
    @DisplayName("비회원이 온라인 스터디를 검색한다.")
    void searchOnlineNotLoginUser() {
        // given
        StudySearchRequest studySearchRequest = new StudySearchRequest("스프링", 1L, true, true);

        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, true, false, TEST_CHILD_CATEGORY);
        study.addTags(Arrays.asList("스프링", "JPA"));
        study.changeImage(TEST_IMAGE);

        PageRequest pageable = PageRequest.of(0, 20);
        Page<Study> studies = new PageImpl<>(Arrays.asList(study), pageable, 1L);

        given(studyQueryRepository.findBySearchCondition(any(), any(), any()))
                .willReturn(studies);

        // when
        Page<StudyResponse> result = studyService.search(null, pageable, studySearchRequest);

        // then
        assertThat(result.getContent().size()).isEqualTo(1);
        then(studyQueryRepository).should(times(1)).findBySearchCondition(any(), any(), any());
    }

    @Test
    @DisplayName("회원이 온라인 스터디를 조회한다.")
    void searchOnline() {
        // given
        StudySearchRequest studySearchRequest = new StudySearchRequest("스프링", 1L, true, false);
        UserResponse user = new UserResponse(1L, "USER", "황주환", "10~19", "male", "이미지 URL", null, 3, "fcmToken");

        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, true, TEST_CHILD_CATEGORY);
        study.addTags(Arrays.asList("스프링", "JPA"));
        study.changeImage(TEST_IMAGE);

        PageRequest pageable = PageRequest.of(0, 20);
        Page<Study> studies = new PageImpl<>(Arrays.asList(study), pageable, 1L);

        given(studyQueryRepository.findBySearchCondition(any(), any(), any()))
                .willReturn(studies);

        // when
        Page<StudyResponse> result = studyService.search(1L, pageable, studySearchRequest);

        // then
        assertThat(result.getContent().size()).isEqualTo(1);
        then(studyQueryRepository).should(times(1)).findBySearchCondition(any(), any(), any());
    }

    @Test
    @DisplayName("지역을 등록한 회원이 오프라인 스터디를 검색한다.")
    void searchOffline() {
        // given
        StudySearchRequest studySearchRequest = new StudySearchRequest("스프링", 1L, false, true);

        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, true, TEST_CHILD_CATEGORY);
        study.addTags(Arrays.asList("스프링", "JPA"));
        study.changeImage(TEST_IMAGE);

        PageRequest pageable = PageRequest.of(0, 20);
        Page<Study> studies = new PageImpl<>(Arrays.asList(study), pageable, 1L);

        given(studyQueryRepository.findBySearchCondition(any(), any(), any()))
                .willReturn(studies);

        given(userServiceClient.findById(any()))
                .willReturn(TEST_USER_RESPONSE);

        given(areaServiceClient.findAroundById(any(), any()))
                .willReturn(Arrays.asList(TEST_AREA_RESPONSE));

        // when
        Page<StudyResponse> result = studyService.search(1L, pageable, studySearchRequest);

        // then
        assertThat(result.getContent().size()).isEqualTo(1);
        then(studyQueryRepository).should(times(1)).findBySearchCondition(any(), any(), any());
        then(userServiceClient).should(times(1)).findById(any());
        then(areaServiceClient).should(times(1)).findAroundById(any(), any());
    }


    @Test
    @DisplayName("회원의 지역이 없는 오프라인 스터디를 조회한다.")
    void searchNotExistUserArea() {
        // given
        StudySearchRequest studySearchRequest = new StudySearchRequest("스프링", 1L, false, true);
        UserResponse user = new UserResponse(1L, "USER", "황주환", "10~19", "male", "이미지 URL", null, 3, "fcmToken");

        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, true, TEST_CHILD_CATEGORY);
        study.addTags(Arrays.asList("스프링", "JPA"));
        study.changeImage(TEST_IMAGE);

        PageRequest pageable = PageRequest.of(0, 20);
        Page<Study> studies = new PageImpl<>(Arrays.asList(study), pageable, 1L);

        given(studyQueryRepository.findBySearchCondition(any(), any(), any()))
                .willReturn(studies);

        given(userServiceClient.findById(any()))
                .willReturn(user);

        // when
        Page<StudyResponse> result = studyService.search(1L, pageable, studySearchRequest);

        // then
        assertThat(result.getContent().size()).isEqualTo(1);
        then(studyQueryRepository).should(times(1)).findBySearchCondition(any(), any(), any());
        then(userServiceClient).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("스터디 참가 대기자를 추가한다.")
    void createWaitUser() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, true, true, null);

        given(studyRepository.findWithWaitUserById(any()))
                .willReturn(Optional.of(study));

        // when
        studyService.createWaitUser(1L, 1L);

        // then
        assertThat(study.getWaitUsers().size()).isEqualTo(1);
        then(studyRepository).should(times(1)).findWithWaitUserById(any());
    }

    @Test
    @DisplayName("스터디 참가 대기자를 삭제한다.")
    void deleteWaitUser() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, true, true, null);
        study.addWaitUser(1L);

        given(studyRepository.findWithWaitUserById(any()))
                .willReturn(Optional.of(study));
        // when
        studyService.deleteWaitUser(1L, 1L);

        // then
        then(studyRepository).should(times(1)).findWithWaitUserById(any());
    }

    @Test
    @DisplayName("스터디 참가 대기자를 거부한다.")
    void failWaitUser() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, true, true, null);
        study.addStudyUser(1L, StudyRole.ADMIN);
        study.addWaitUser(2L);

        given(studyRepository.findWithWaitUserById(any()))
                .willReturn(Optional.of(study));

        willDoNothing()
                .given(studyApplyFailMessageSender)
                .send(any());

        // when
        studyService.failWaitUser(1L, 1L, 2L);

        // then
        then(studyRepository).should(times(1)).findWithWaitUserById(any());
        then(studyApplyFailMessageSender).should(times(1)).send(any());
    }

    @Test
    @DisplayName("스터디 참가 대기자 리스트를 조회한다.")
    void findWaitUsersById() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, true, true, null);
        study.addWaitUser(1L);

        given(studyRepository.findWithWaitUserById(any()))
                .willReturn(Optional.of(study));

        given(userServiceClient.findByIdIn(any()))
                .willReturn(Arrays.asList(TEST_USER_RESPONSE));

        // when
        List<UserResponse> result = studyService.findWaitUsersById(1L);

        // then
        assertThat(result.size()).isEqualTo(1);
        then(studyRepository).should(times(1)).findWithWaitUserById(any());
        then(userServiceClient).should(times(1)).findByIdIn(any());
    }

    @Test
    @DisplayName("스터디 참가자를 추가한다.")
    void createStudyUser() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, true, true, null);
        study.addStudyUser(1L, StudyRole.ADMIN);
        study.addWaitUser(2L);

        given(studyRepository.findWithWaitUserById(any()))
                .willReturn(Optional.of(study));

        willDoNothing()
                .given(studyApplySuccessMessageSender)
                .send(any());
        // when
        studyService.createStudyUser(1L, 1L, 2L);

        // then
        then(studyRepository).should(times(1)).findWithWaitUserById(any());
        then(studyApplySuccessMessageSender).should(times(1)).send(any());
    }

    @Test
    @DisplayName("스터디 관리자가 스터디 참가자를 삭제한다.")
    void deleteStudyUser() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, true, true, null);
        study.addStudyUser(1L, StudyRole.ADMIN);
        study.addStudyUser(2L, StudyRole.USER);

        given(studyRepository.findWithStudyUserById(any()))
                .willReturn(Optional.of(study));
        // when
        studyService.deleteStudyUser(1L, 1L, 2L);

        // then
        then(studyRepository).should(times(1)).findWithStudyUserById(any());
    }

    @Test
    @DisplayName("스터디 참가자가 탈퇴한다.")
    void deleteStudyUserSelf() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, true, true, null);
        study.addStudyUser(1L, StudyRole.ADMIN);
        study.addStudyUser(2L, StudyRole.USER);

        given(studyRepository.findWithStudyUserById(any()))
                .willReturn(Optional.of(study));
        // when
        studyService.deleteStudyUser(1L, 1L);

        // then
        then(studyRepository).should(times(1)).findWithStudyUserById(any());
    }

    @Test
    @DisplayName("스터디 참가자 리스트를 조회한다.")
    void findStudyUsersById() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, true, true, null);
        study.addStudyUser(1L, StudyRole.ADMIN);

        given(studyRepository.findWithStudyUserById(any()))
                .willReturn(Optional.of(study));

        given(userServiceClient.findByIdIn(any()))
                .willReturn(Arrays.asList(TEST_USER_RESPONSE));

        // when
        List<StudyUserResponse> result = studyService.findStudyUsersById(1L);

        // then
        assertThat(result.size()).isEqualTo(1);
        then(studyRepository).should(times(1)).findWithStudyUserById(any());
        then(userServiceClient).should(times(1)).findByIdIn(any());
    }

    @Test
    @DisplayName("스터디 채팅방을 생성한다.")
    void createChatRoom() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, true, true, null);
        study.addStudyUser(1L, StudyRole.ADMIN);

        given(studyRepository.findWithChatRoomById(any()))
                .willReturn(Optional.of(study));
        // when
        studyService.createChatRoom(1L, 1L, TEST_CHAT_ROOM_CREATE_REQUEST);

        // then
        then(studyRepository).should(times(1)).findWithChatRoomById(any());
    }

    @Test
    @DisplayName("스터디 채팅방을 수정한다.")
    void updateChatRoom() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, true, true, null);
        study.addStudyUser(1L, StudyRole.ADMIN);
        study.getChatRooms().add(new ChatRoom(1L, "공지사항", study));

        given(studyRepository.findWithChatRoomById(any()))
                .willReturn(Optional.of(study));
        // when
        studyService.updateChatRoom(1L, 1L, 1L, TEST_CHAT_ROOM_UPDATE_REQUEST);

        // then
        then(studyRepository).should(times(1)).findWithChatRoomById(any());
    }

    @Test
    @DisplayName("스터디 채팅방을 삭제한다.")
    void deleteChatRoom() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, true, true, null);
        study.addStudyUser(1L, StudyRole.ADMIN);
        study.getChatRooms().add(new ChatRoom(1L, "공지사항", study));

        given(studyRepository.findWithChatRoomById(any()))
                .willReturn(Optional.of(study));

        // when
        studyService.deleteChatRoom(1L, 1L, 1L);

        // then
        then(studyRepository).should(times(1)).findWithChatRoomById(any());
    }

    @Test
    @DisplayName("스터디 채팅방을 조회한다.")
    void findChatRoomsById() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, true, true, null);
        study.addChatRoom("공지사항");

        given(studyRepository.findWithChatRoomById(any()))
                .willReturn(Optional.of(study));
        // when
        List<ChatRoomResponse> result = studyService.findChatRoomsById(1L);

        // then
        assertThat(result.size()).isEqualTo(1);
        then(studyRepository).should(times(1)).findWithChatRoomById(any());
    }

    @Test
    @DisplayName("회원이 가입된 스터디를 조회한다.")
    void findByUserId() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, true, true, null);

        given(studyQueryRepository.findByUserId(any()))
                .willReturn(Arrays.asList(study));

        // when
        List<StudyResponse> result = studyService.findByUserId(1L);

        // then
        assertThat(result.size()).isEqualTo(1);
        then(studyQueryRepository).should(times(1)).findByUserId(any());
    }

    @Test
    @DisplayName("회원이 가입 신청한 스터디를 조회한다.")
    void findByWaitUserId() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, true, true, null);
        study.changeImage(TEST_IMAGE);
        study.addWaitUser(1L);
        study.getTags().add(new Tag(1L, "스프링", study));

        given(studyQueryRepository.findWithWaitUserByUserId(any()))
                .willReturn(Arrays.asList(study));

        // when
        List<StudyResponse> result = studyService.findByWaitUserId(1L);

        // then
        assertThat(result.size()).isEqualTo(1L);
        assertThat(result.get(0).getWaitStatus()).isEqualTo(WaitStatus.WAIT);
        then(studyQueryRepository).should(times(1)).findWithWaitUserByUserId(any());
    }

    @Test
    @DisplayName("스터디 참가자와 스터디 대기자에서 회원을 삭제한다.")
    void deleteStudyUserAndWaitUser() {
        // given
        Study study1 = Study.createStudy("스프링 스터디", "스프링 스터디", 5, true, true, null);
        study1.addWaitUser(1L);

        Study study2 = Study.createStudy("스프링 스터디", "스프링 스터디", 5, true, true, null);
        study2.addStudyUser(1L, StudyRole.ADMIN);

        given(studyQueryRepository.findWithWaitUserByUserId(any()))
                .willReturn(Arrays.asList(study1));

        given(studyQueryRepository.findWithStudyUsersByUserId(any()))
                .willReturn(Arrays.asList(study2));

        // when
        studyService.deleteStudyUserAndWaitUser(UserDeleteMessage.from(1L));

        // then
        then(studyQueryRepository).should(times(1)).findWithWaitUserByUserId(any());
        then(studyQueryRepository).should(times(1)).findWithStudyUsersByUserId(any());
    }

    @Test
    @DisplayName("스터디 태그를 추가한다.")
    void addTag() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, true, true, null);
        study.addStudyUser(1L, StudyRole.ADMIN);

        given(studyRepository.findWithStudyUserById(any()))
                .willReturn(Optional.of(study));

        // when
        studyService.addTag(1L, 1L, TEST_TAG_CREATE_REQUEST);

        // then
        then(studyRepository).should(times(1)).findWithStudyUserById(any());
    }

    @Test
    @DisplayName("스터디 태그를 삭제한다.")
    void deleteTag() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, true, true, null);
        study.addStudyUser(1L, StudyRole.ADMIN);
        study.getTags().add(new Tag(1L, "스프링", study));

        given(studyRepository.findWithStudyUserById(any()))
                .willReturn(Optional.of(study));

        // when
        studyService.deleteTag(1L, 1L, 1L);

        // then
        then(studyRepository).should(times(1)).findWithStudyUserById(any());
    }

}