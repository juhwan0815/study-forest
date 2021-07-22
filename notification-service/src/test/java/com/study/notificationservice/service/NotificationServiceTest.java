package com.study.notificationservice.service;

import com.study.notificationservice.NotificationFixture;
import com.study.notificationservice.client.StudyServiceClient;
import com.study.notificationservice.client.UserServiceClient;
import com.study.notificationservice.fcm.FcmMessageSender;
import com.study.notificationservice.repository.NotificationRepository;
import com.study.notificationservice.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static com.study.notificationservice.NotificationFixture.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private FcmMessageSender fcmMessageSender;

    @Mock
    private StudyServiceClient studyServiceClient;

    @Mock
    private UserServiceClient userServiceClient;

    @Test
    @DisplayName("모임 생성 알림을 생성한다.")
    void createGatheringNotification(){
        // given
        given(studyServiceClient.findWithStudyUserByStudyId(any()))
                .willReturn(TEST_STUDY_RESPONSE);

        willDoNothing()
                .given(fcmMessageSender)
                .send(any(),any(),any());

        given(notificationRepository.save(any()))
                .willReturn(null)
                .willReturn(null);

        // when
        notificationService.gatheringCreate(TEST_GATHERING_CREATE_MESSAGE);

        // then
        then(studyServiceClient).should(times(1)).findWithStudyUserByStudyId(any());
        then(fcmMessageSender).should(times(2)).send(any(),any(),any());
        then(notificationRepository).should(times(2)).save(any());
    }

    @Test
    @DisplayName("스터디 가입 실패 알림을 생성한다.")
    void createStudyApplyFailNotification(){
        // given
        given(userServiceClient.findUserById(any()))
            .willReturn(TEST_USER_RESPONSE);

        willDoNothing()
                .given(fcmMessageSender)
                .send(any(),any(),any());

        given(notificationRepository.save(any()))
                .willReturn(null);

        // when
        notificationService.studyApplyFail(TEST_STUDY_APPLY_FAIL_MESSAGE);

        // then
        then(userServiceClient).should(times(1)).findUserById(any());
        then(fcmMessageSender).should(times(1)).send(any(),any(),any());
        then(notificationRepository).should(times(1)).save(any());
    }

    @Test
    @DisplayName("스터디 가입 성공 알림을 생성한다.")
    void createStudyApplySuccessNotification(){
        // given
        given(userServiceClient.findUserById(any()))
                .willReturn(TEST_USER_RESPONSE);

        willDoNothing()
                .given(fcmMessageSender)
                .send(any(),any(),any());

        given(notificationRepository.save(any()))
                .willReturn(null);

        // when
        notificationService.studyApplySuccess(TEST_STUDY_APPLY_SUCCESS_MESSAGE);

        // then
        then(userServiceClient).should(times(1)).findUserById(any());
        then(fcmMessageSender).should(times(1)).send(any(),any(),any());
        then(notificationRepository).should(times(1)).save(any());
    }

    @Test
    @DisplayName("스터디 생성 알림을 생성한다.")
    void createStudyNotification(){
        // given
        given(userServiceClient.findWithInterestTagsByTagIdList(any()))
                .willReturn(Arrays.asList(TEST_USER_WITH_TAG_RESPONSE1,TEST_USER_WITH_TAG_RESPONSE2));

        willDoNothing()
                .given(fcmMessageSender)
                .send(any(),any(),any());

        given(notificationRepository.save(any()))
                .willReturn(null)
                .willReturn(null)
                .willReturn(null)
                .willReturn(null);

        // when
        notificationService.studyCreate(TEST_STUDY_CREATE_MESSAGE);

        // then
        then(userServiceClient).should(times(1)).findWithInterestTagsByTagIdList(any());
        then(fcmMessageSender).should(times(4)).send(any(),any(),any());
        then(notificationRepository).should(times(4)).save(any());
    }

    @Test
    @DisplayName("채팅 알림을 생성한다.")
    void createChatNotification(){
        // given
        given(studyServiceClient.findWithStudyUserByStudyId(any()))
                .willReturn(TEST_STUDY_RESPONSE);

        willDoNothing()
                .given(fcmMessageSender)
                .send(any(),any(),any());

        // when
        notificationService.chatCreate(TEST_CHAT_CREATE_MESSAGE);

        // then
        then(studyServiceClient).should(times(1)).findWithStudyUserByStudyId(any());
        then(fcmMessageSender).should(times(2)).send(any(),any(),any());
    }

}
