package com.study.chatservice.service;

import com.study.chatservice.ChatRoomFixture;
import com.study.chatservice.domain.ChatMessage;
import com.study.chatservice.domain.ChatRoom;
import com.study.chatservice.model.chatMessage.ChatMessageResponse;
import com.study.chatservice.repository.ChatMessageRepository;
import com.study.chatservice.repository.ChatRoomRepository;
import com.study.chatservice.service.impl.ChatMessageServiceImpl;
import com.study.chatservice.service.impl.ChatRoomServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceTest {

    @InjectMocks
    private ChatMessageServiceImpl chatMessageService;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Test
    @DisplayName("채팅방 ID로 채팅을 조회한다.")
    void findByChatRoomId(){
        // given
        List<ChatMessage> content = new ArrayList<>();
        content.add(ChatRoomFixture.TEST_CHAT_MESSAGE3);
        content.add(ChatRoomFixture.TEST_CHAT_MESSAGE2);
        content.add(ChatRoomFixture.TEST_CHAT_MESSAGE1);

        PageRequest pageable = PageRequest.of(0, 10);
        Page<ChatMessage> pageData = new PageImpl<>(content,pageable,content.size());

        given(chatMessageRepository.findByChatRoomIdOrderByCreatedAtDesc(any(),any()))
                .willReturn(pageData);

        // when
        Page<ChatMessageResponse> result = chatMessageService.findByChatRoomId(1L, pageable);

        // then
        assertThat(result.getContent().size()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getTotalElements()).isEqualTo(3);

        then(chatMessageRepository).should(times(1))
                .findByChatRoomIdOrderByCreatedAtDesc(any(),any());
    }

}