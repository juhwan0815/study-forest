package com.study.kafka.listener;

import com.study.kakfa.StudyDeleteMessage;
import com.study.service.MessageService;
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
class StudyDeleteListenerTest {

    @InjectMocks
    private StudyDeleteListener studyDeleteListener;

    @Mock
    private MessageService messageService;

    @Test
    @DisplayName("스터디 삭제 메세지를 수신한다.")
    void studyDeleteListen() {
        // given
        willDoNothing()
                .given(messageService)
                .deleteMessage(any(StudyDeleteMessage.class));

        // when
        studyDeleteListener.studyDeleteListen(StudyDeleteMessage.from(1L, Arrays.asList(1L, 2L)),null);

        // then
        then(messageService).should(times(1)).deleteMessage(any(StudyDeleteMessage.class));
    }
}