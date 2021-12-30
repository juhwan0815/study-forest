package com.study.kafka.listener;

import com.study.kakfa.GatheringCreateMessage;
import com.study.service.NotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class GatheringCreateListenerTest {

    @InjectMocks
    private GatheringCreateListener gatheringCreateListener;

    @Mock
    private NotificationService notificationService;

    @Test
    @DisplayName("모임 생성 메세지를 수신한다.")
    void gatheringCreateListen() {
        // given
        willDoNothing()
                .given(notificationService)
                .gatheringCreate(any());

        // when
        gatheringCreateListener.gatheringCreateListen(GatheringCreateMessage.from(1L, LocalDateTime.now(), true, "스프링 스터디"), null);

        // then
        then(notificationService).should(times(1)).gatheringCreate(any());
    }
}