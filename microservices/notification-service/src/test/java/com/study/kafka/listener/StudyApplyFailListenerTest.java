package com.study.kafka.listener;

import com.study.kakfa.MessageCreateMessage;
import com.study.kakfa.StudyApplyFailMessage;
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
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class StudyApplyFailListenerTest {

    @InjectMocks
    private StudyApplyFailListener studyApplyFailListener;

    @Mock
    private NotificationService notificationService;

    @Test
    @DisplayName("스터디 참가 실패 메세지를 수신한다.")
    void studyApplyFailListen() {
        // given
        willDoNothing()
                .given(notificationService)
                .studyApplyFail(any());

        // when
        studyApplyFailListener.studyApplyFailListen(StudyApplyFailMessage.from(1L, 1L, "스프링 스터디"), null);

        // then
        then(notificationService).should(times(1)).studyApplyFail(any());
    }
}