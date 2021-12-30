package com.study.kafka.listener;

import com.study.kakfa.StudyCreateMessage;
import com.study.service.NotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class StudyCreateListenerTest {

    @InjectMocks
    private StudyCreateListener studyCreateListener;

    @Mock
    private NotificationService notificationService;

    @Test
    @DisplayName("스터디 생성 메세지를 수신한다.")
    void studyCreateListen() {
        // given
        willDoNothing()
                .given(notificationService)
                .studyCreate(any());

        // when
        studyCreateListener.studyCreateListen(StudyCreateMessage.from(1L, "스프링 스터디", Arrays.asList("스프링")), null);

        // then
        then(notificationService).should(times(1)).studyCreate(any());
    }
}