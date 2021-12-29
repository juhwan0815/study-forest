package com.study.kafka.listener;

import com.study.kakfa.ChatRoomDeleteMessage;
import com.study.service.MessageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ChatRoomDeleteListenerTest {

    @InjectMocks
    private ChatRoomDeleteListener chatRoomDeleteListener;

    @Mock
    private MessageService messageService;

    @Test
    @DisplayName("채팅방 삭제 메세지를 수신한다.")
    void chatRoomDeleteListen() {
        // given
        willDoNothing()
                .given(messageService)
                .deleteMessage(any(ChatRoomDeleteMessage.class));

        // when
        chatRoomDeleteListener.chatRoomDeleteListen(ChatRoomDeleteMessage.from(1L), null);

        // then
        then(messageService).should(times(1)).deleteMessage(any(ChatRoomDeleteMessage.class));
    }
}