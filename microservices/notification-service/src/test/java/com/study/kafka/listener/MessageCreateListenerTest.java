package com.study.kafka.listener;

import com.study.kakfa.GatheringCreateMessage;
import com.study.kakfa.MessageCreateMessage;
import com.study.service.NotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class MessageCreateListenerTest {

    @InjectMocks
    private MessageCreateListener messageCreateListener;

    @Mock
    private NotificationService notificationService;

    @Test
    @DisplayName("채팅 메세지 생성을 수신한다.")
    void messageCreateListen() {
        // given
        willDoNothing()
                .given(notificationService)
                .messageCreate(any());

        // when
        messageCreateListener.messageCreateListen(MessageCreateMessage.from(1L, "황주환", "스프링 스터디", Arrays.asList(1L,2L)), null);

        // then
        then(notificationService).should(times(1)).messageCreate(any());
    }
}