package com.study.service;

import com.study.NotificationFixture;
import com.study.client.StudyServiceClient;
import com.study.client.UserServiceClient;
import com.study.domain.Notification;
import com.study.dto.NotificationResponse;
import com.study.fcm.FcmMessageSender;
import com.study.kafka.listener.StudyApplySuccessListener;
import com.study.kakfa.*;
import com.study.repository.NotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDateTime;
import java.util.Arrays;

import static com.study.NotificationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private StudyServiceClient studyServiceClient;

    @Mock
    private FcmMessageSender fcmMessageSender;

    @Test
    @DisplayName("회원의 알림을 조회한다.")
    void findByUserId() {
        Slice<Notification> notifications
                = new SliceImpl<>(Arrays.asList(TEST_NOTIFICATION), PageRequest.of(0, 20), false);

        // given
        given(notificationRepository.findByUserIdOrderById(any(), any()))
                .willReturn(notifications);

        // when
        Slice<NotificationResponse> result = notificationService.findByUserId(1L, PageRequest.of(0, 20));

        // then
        assertThat(result.getContent().size()).isEqualTo(1);
        then(notificationRepository).should(times(1)).findByUserIdOrderById(any(), any());
    }

    @Test
    @DisplayName("스터디 참가 실패 알림을 보낸다.")
    void studyApplyFail() {
        // given
        given(userServiceClient.findById(any()))
                .willReturn(TEST_USER_RESPONSE);

        given(notificationRepository.save(any()))
                .willReturn(null);

        willDoNothing()
                .given(fcmMessageSender)
                .send(any(), any(), any());

        // when
        notificationService.studyApplyFail(StudyApplyFailMessage.from(1L, 1L, "테스트 이름"));

        // then
        then(userServiceClient).should(times(1)).findById(any());
        then(notificationRepository).should(times(1)).save(any());
        then(fcmMessageSender).should(times(1)).send(any(), any(), any());
    }

    @Test
    @DisplayName("스터디 참가 성공 알림을 보낸다.")
    void studyApplySuccess() {
        // given
        given(userServiceClient.findById(any()))
                .willReturn(TEST_USER_RESPONSE);

        given(notificationRepository.save(any()))
                .willReturn(null);

        willDoNothing()
                .given(fcmMessageSender)
                .send(any(), any(), any());

        // when
        notificationService.studyApplySuccess(StudyApplySuccessMessage.from(1L, 1L, "테스트 이름"));

        // then
        then(userServiceClient).should(times(1)).findById(any());
        then(notificationRepository).should(times(1)).save(any());
        then(fcmMessageSender).should(times(1)).send(any(), any(), any());
    }

    @Test
    @DisplayName("모임을 생성한다.")
    void gatheringCreate() {
        // given
        given(studyServiceClient.findById(any()))
                .willReturn(TEST_STUDY_RESPONSE);

        given(studyServiceClient.findStudyUsersById(any()))
                .willReturn(Arrays.asList(TEST_USER_RESPONSE));

        given(notificationRepository.save(any()))
                .willReturn(null);

        willDoNothing()
                .given(fcmMessageSender)
                .send(any(), any(), any());
        // when
        notificationService.gatheringCreate(GatheringCreateMessage.from(1L, LocalDateTime.now(), false, "테스트 내용"));

        // then
        then(studyServiceClient).should(times(1)).findById(any());
        then(studyServiceClient).should(times(1)).findStudyUsersById(any());
        then(notificationRepository).should(times(1)).save(any());
        then(fcmMessageSender).should(times(1)).send(any(), any(), any());
    }

    @Test
    @DisplayName("메세지 생성 알림을 전송한다.")
    void messageCreate() {
        // given
        given(studyServiceClient.findByChatRoomId(any()))
                .willReturn(TEST_STUDY_RESPONSE);

        given(studyServiceClient.findChatRoomByIdAndChatRoomId(any(), any()))
                .willReturn(TEST_CHATROOM_RESPONSE);

        given(studyServiceClient.findStudyUsersById(any()))
                .willReturn(Arrays.asList(TEST_USER_RESPONSE));

        willDoNothing()
                .given(fcmMessageSender)
                .send(any(), any(), any());
        // when
        notificationService.messageCreate(MessageCreateMessage.from(1L, "황주환", "테스트 내용", Arrays.asList(2L)));

        // then
        then(studyServiceClient).should(times(1)).findByChatRoomId(any());
        then(studyServiceClient).should(times(1)).findChatRoomByIdAndChatRoomId(any(), any());
        then(studyServiceClient).should(times(1)).findStudyUsersById(any());
        then(fcmMessageSender).should(times(1)).send(any(), any(), any());
    }

    @Test
    @DisplayName("메세지 생성 알림을 전송하지 않는다..")
    void messageCreateNotSend() {
        // given
        given(studyServiceClient.findByChatRoomId(any()))
                .willReturn(TEST_STUDY_RESPONSE);

        given(studyServiceClient.findChatRoomByIdAndChatRoomId(any(), any()))
                .willReturn(TEST_CHATROOM_RESPONSE);

        given(studyServiceClient.findStudyUsersById(any()))
                .willReturn(Arrays.asList(TEST_USER_RESPONSE));

        // when
        notificationService.messageCreate(MessageCreateMessage.from(1L, "황주환", "테스트 내용", Arrays.asList(1L)));

        // then
        then(studyServiceClient).should(times(1)).findByChatRoomId(any());
        then(studyServiceClient).should(times(1)).findChatRoomByIdAndChatRoomId(any(), any());
        then(studyServiceClient).should(times(1)).findStudyUsersById(any());
    }

    @Test
    @DisplayName("스터디 생성 알림을 전송한다.")
    void studyCreate() {
        // given
        given(userServiceClient.findByKeywordContentContain(any()))
                .willReturn(Arrays.asList(TEST_USER_RESPONSE))
                .willReturn(Arrays.asList(TEST_USER_RESPONSE));

        given(notificationRepository.save(any()))
                .willReturn(null)
                .willReturn(null);

        willDoNothing().
                given(fcmMessageSender)
                .send(any(), any(), any());


        // when
        notificationService.studyCreate(StudyCreateMessage.from(1L, "스프링 스터디", Arrays.asList("스프링", "JPA")));

        // then
        then(userServiceClient).should(times(2)).findByKeywordContentContain(any());
        then(notificationRepository).should(times(2)).save(any());
        then(fcmMessageSender).should(times(2)).send(any(),any(),any());
    }
}