package com.study.chatservice.service.impl;

import com.study.chatservice.ChatRoomFixture;
import com.study.chatservice.model.chatroom.ChatRoomResponse;
import com.study.chatservice.repository.ChatRoomRepository;
import com.study.chatservice.service.ChatRoomService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.study.chatservice.ChatRoomFixture.*;
import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

    @InjectMocks
    private ChatRoomServiceImpl chatRoomService;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Test
    @DisplayName("채팅방을 생성한다.")
    void createChatRoom(){
        // given
        given(chatRoomRepository.findByNameAndAndStudyId(any(),any()))
                .willReturn(Optional.empty());

        given(chatRoomRepository.save(any()))
                .willReturn(null);

        // when
        ChatRoomResponse result = chatRoomService.create(1L, TEST_CHAT_ROOM_CREATE_REQUEST);

        // then
        assertThat(result.getName()).isEqualTo(TEST_CHAT_ROOM_CREATE_REQUEST.getName());
        assertThat(result.getStudyId()).isEqualTo(1L);
    }

}