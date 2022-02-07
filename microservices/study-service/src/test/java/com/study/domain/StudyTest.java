package com.study.domain;

import com.study.exception.BusinessException;
import com.study.exception.DuplicateException;
import com.study.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StudyTest {

    @Test
    @DisplayName("스터디를 생성한다.")
    void createStudy() {
        // given
        Category category = Category.createCategory("개발", null);

        String name = "스프링 스터디";
        String content = "스프링 스터디";
        int numberOfPeople = 5;
        boolean online = true;
        boolean offline = true;
        String imageUrl = "imageUrl";

        // when
        Study result = Study.createStudy(name, content, numberOfPeople, online, offline, imageUrl, category);

        // then
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getContent()).isEqualTo(content);
        assertThat(result.isOffline()).isEqualTo(offline);
        assertThat(result.isOnline()).isEqualTo(online);
        assertThat(result.getNumberOfPeople()).isEqualTo(numberOfPeople);
        assertThat(result.getCurrentNumberOfPeople()).isEqualTo(0);
        assertThat(result.getStatus()).isEqualTo(StudyStatus.OPEN);
        assertThat(result.getImageUrl()).isEqualTo(imageUrl);
        assertThat(result.getCategory()).isEqualTo(category);
    }

    @Test
    @DisplayName("스터디를 수정한다.")
    void update() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, false, null, null);
        Category category = Category.createCategory("개발", null);

        // when
        study.update("백엔드 스터디", "백엔드 스터디", 3, true, true, false, "imageUrl", category);

        // then
        assertThat(study.getName()).isEqualTo("백엔드 스터디");
        assertThat(study.getCategory()).isEqualTo(category);
        assertThat(study.getContent()).isEqualTo("백엔드 스터디");
        assertThat(study.isOnline()).isEqualTo(true);
        assertThat(study.isOffline()).isEqualTo(true);
        assertThat(study.getNumberOfPeople()).isEqualTo(3);
        assertThat(study.getImageUrl()).isEqualTo("imageUrl");
        assertThat(study.getStatus()).isEqualTo(StudyStatus.CLOSE);
    }

    @Test
    @DisplayName("스터디의 현재인원보다 낮게 정원을 바꾸면 예외가 발생한다.")
    void changeManyNumOfPeople() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, false, null, null);
        study.addStudyUser(1L, StudyRole.ADMIN);
        study.addStudyUser(2L, StudyRole.USER);

        // when
        assertThrows(BusinessException.class,
                () -> study.update("백엔드 스터디", "백엔드 스터디", 1, true, true, true, "imageUrl", null));
    }

    @Test
    @DisplayName("스터디를 OPEN 상태로 변경한다.")
    void changeStatusOpen() {
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, false, null, null);

        // when
        study.update("백엔드 스터디", "백엔드 스터디", 3, true, true, true, null, null);

        // then
        assertThat(study.getStatus()).isEqualTo(StudyStatus.OPEN);
    }

    @Test
    @DisplayName("스터디 참가자를 추가한다.")
    void addStudyUser() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, false, "imageUrl", null);

        // when
        study.addStudyUser(1L, StudyRole.USER);

        // then
        assertThat(study.getStudyUsers().size()).isEqualTo(1);
        assertThat(study.getCurrentNumberOfPeople()).isEqualTo(1);
    }

    @Test
    @DisplayName("스터디 태그둘을 변경한다.")
    void changeTags() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, false, "imageUrl", null);

        // when
        study.changeTags(Arrays.asList("스프링", "자바"));

        // then
        assertThat(study.getTags().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("스터디 지역을 변경한다.")
    void changeArea() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, false, "imageUrl", null);

        // when
        study.changeArea(1L);

        // then
        assertThat(study.getAreaId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("스터디 관리자인지 확인한다.")
    void isStudyAdmin() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, false, "imageUrl", null);
        study.addStudyUser(1L, StudyRole.ADMIN);

        // when
        boolean result = study.isStudyAdmin(1L);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("스터디 관리자인지 확인하고 아니면 false 를 반환한다.")
    void isNotStudyAdmin() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, false, "imageUrl", null);
        study.addStudyUser(1L, StudyRole.USER);
        study.addStudyUser(2L, StudyRole.ADMIN);

        // when
        boolean result = study.isStudyAdmin(1L);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("스터디 참가 대기자를 추가한다.")
    void addWaitUser() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, false, "imageUrl", null);
        study.addStudyUser(1L, StudyRole.ADMIN);

        // when
        study.addWaitUser(2L);

        // then
        assertThat(study.getWaitUsers().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("추가된 스터디 참가 대기자를 추가하면 예외가 발생한다.")
    void addWaitUserDuplicate() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, false, "imageUrl", null);
        study.addWaitUser(1L);

        // when
        assertThrows(DuplicateException.class, () -> study.addWaitUser(1L));
    }

    @Test
    @DisplayName("이미 스터디 참가자를 참가 대기자에 추가하면 예외가 발생한다.")
    void addStudyUserDuplicate() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, false, "imageUrl", null);
        study.addStudyUser(1L, StudyRole.ADMIN);

        // when
        assertThrows(DuplicateException.class, () -> study.addWaitUser(1L));
    }

    @Test
    @DisplayName("스터디 참가를 거부한다.")
    void failWaitUser() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, false, "imageUrl", null);
        study.addWaitUser(1L);

        // when
        study.failWaitUser(1L);

        // then
        assertThat(study.getWaitUsers().get(0).getStatus()).isEqualTo(WaitStatus.FAIL);
    }

    @Test
    @DisplayName("존재하지 않는 스터디 참가를 거부하면 예외가 발생한다.")
    void failWaitUserNotFound() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, false, "imageUrl", null);

        // when
        assertThrows(NotFoundException.class, () -> study.failWaitUser(1L));
    }

    @Test
    @DisplayName("스터디 참가를 취소한다.")
    void deleteWaitUser() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, false, "imageUrl", null);
        study.addWaitUser(1L);

        // when
        study.deleteWaitUser(1L);

        // then
        assertThat(study.getWaitUsers().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("존재하지 않는 스터디 참가를 취소하면 예외가 발생한다.")
    void deleteWaitUserNotFound() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, false, "imageUrl", null);

        // when
        assertThrows(NotFoundException.class, () -> study.deleteWaitUser(1L));
    }

    @Test
    @DisplayName("스터디 참가 대기자의 ID 리스트를 반환한다.")
    void getWaitUsersId() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, false, "imageUrl", null);
        study.addWaitUser(1L);
        study.addWaitUser(2L);
        study.successWaitUser(2L);

        // when
        List<Long> result = study.getWaitUsersId();

        // then
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("스터디 참가를 성공 처리한다.")
    void successWaitUser() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, false, "imageUrl", null);
        study.addWaitUser(1L);

        // when
        study.successWaitUser(1L);

        // then
        assertThat(study.getWaitUsers().get(0).getStatus()).isEqualTo(WaitStatus.SUCCESS);
    }

    @Test
    @DisplayName("존재하지 않는 스터디 참가를 성공 처리하면 예외가 발생한다.")
    void successWaitUserNotFound() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, false, "imageUrl", null);

        // when
        assertThrows(NotFoundException.class, () -> study.successWaitUser(1L));
    }

    @Test
    @DisplayName("스터디 회원이 탈퇴한다.")
    void deleteStudyUser() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, false, "imageUrl", null);
        study.addStudyUser(1L, StudyRole.ADMIN);

        // when
        study.deleteStudyUser(1L);

        // then
        assertThat(study.getStudyUsers().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("존재하지 않는 스터디 회원이 탈퇴하면 예외가 발생한다.")
    void deleteStudyUserNotFound() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, false, "imageUrl", null);

        // when
        assertThrows(NotFoundException.class, () -> study.deleteStudyUser(1L));
    }

    @Test
    @DisplayName("스터디 참가자 ID 리스트를 반환한다.")
    void getStudyUsersId() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, false, "imageUrl", null);
        study.addStudyUser(1L, StudyRole.ADMIN);
        study.addStudyUser(2L, StudyRole.USER);

        // when
        List<Long> result = study.getStudyUsersId();

        // then
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("스터디 채팅방을 추가한다.")
    void addChatRoom() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, false, "imageUrl", null);

        // when
        study.addChatRoom("공지사항");

        // then
        assertThat(study.getChatRooms().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("중복된 스터디 채팅방을 추가하면 예외가 발생한다.")
    void addChatRoomDuplicate() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, false, "imageUrl", null);
        study.addChatRoom("공지사항");

        // when
        assertThrows(DuplicateException.class, () -> study.addChatRoom("공지사항"));
    }

    @Test
    @DisplayName("스터디 채팅방 이름을 수정한다.")
    void updateChatRoom() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, false, "imageUrl", null);
        study.getChatRooms().add(new ChatRoom(1L, "공지사항", study));

        // when
        study.updateChatRoom(1L, "대화방");

        // then
        assertThat(study.getChatRooms().get(0).getName()).isEqualTo("대화방");
    }

    @Test
    @DisplayName("존재하지 않는 스터디 채팅방 이름을 수정하면 예외가 발생한다.")
    void updateChatRoomNotFound() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, false, "imageUrl", null);

        // when
        assertThrows(NotFoundException.class, () -> study.updateChatRoom(1L, "대화방"));
    }

    @Test
    @DisplayName("스터디 채팅방 이름을 중복으로 수정하면 예외가 발생한다.")
    void updateChatRoomDuplicate() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, false, "imageUrl", null);
        study.getChatRooms().add(new ChatRoom(1L, "공지사항", study));

        // when
        assertThrows(DuplicateException.class, () -> study.updateChatRoom(1L, "공지사항"));
    }

    @Test
    @DisplayName("스터디 채팅방을 삭제한다.")
    void deleteChatRoom() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, false, "imageUrl", null);
        study.getChatRooms().add(new ChatRoom(1L, "공지사항", study));

        // when
        study.deleteChatRoom(1L);

        // then
        assertThat(study.getChatRooms().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("존재하지 않는 스터디 채팅방을 삭제하면 예외가 발생한다.")
    void deleteChatRoomNotFound() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, false, "imageUrl", null);

        // when
        assertThrows(NotFoundException.class, () -> study.deleteChatRoom(1L));
    }

    @Test
    @DisplayName("스터디 채팅방 Id 리스트를 반환한다.")
    void getChatRoomsId() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, false, "imageUrl", null);
        study.getChatRooms().add(new ChatRoom(1L, "공지사항", study));

        // when
        List<Long> result = study.getChatRoomsId();

        // then
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("스터디 채팅방을 반환한다.")
    void getChatRoom() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, false, "imageUrl", null);
        study.getChatRooms().add(new ChatRoom(1L, "공지사항", study));

        // when
        ChatRoom result = study.getChatRoom(1L);

        // then
        assertThat(result.getName()).isEqualTo("공지사항");
    }

    @Test
    @DisplayName("스터디 채팅방을 반환할 때 존재하지 않으면 예외가 발생한다.")
    void getChatRoomNotFound() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, false, "imageUrl", null);

        // when
        assertThrows(NotFoundException.class, () -> study.getChatRoom(1L));
    }
}