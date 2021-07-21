package com.study.notificationservice.service;

import com.study.notificationservice.NotificationFixture;
import com.study.notificationservice.client.StudyServiceClient;
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
}
