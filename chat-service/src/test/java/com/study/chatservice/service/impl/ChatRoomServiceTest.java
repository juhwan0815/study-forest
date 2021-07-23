package com.study.chatservice.service.impl;

import com.study.chatservice.ChatRoomFixture;
import com.study.chatservice.domain.ChatRoom;
import com.study.chatservice.exception.ChatRoomException;
import com.study.chatservice.model.chatroom.ChatRoomResponse;
import com.study.chatservice.repository.ChatMessageRepository;
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
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

    @InjectMocks
    private ChatRoomServiceImpl chatRoomService;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

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

    @Test
    @DisplayName("예외 테스트 : 스터디에 중복된 이름의 채팅방이 존재할 경우 예외가 발생한다.")
    void createDuplicatedChatRoom(){
        // given
        given(chatRoomRepository.findByNameAndAndStudyId(any(),any()))
                .willReturn(Optional.of(TEST_CHAT_ROOM));

        // when
        assertThrows(ChatRoomException.class,()-> chatRoomService.create(1L,TEST_CHAT_ROOM_CREATE_REQUEST));
    }

    @Test
    @DisplayName("채팅방 이름을 변경한다.")
    void updateChatRoom(){
        // given
        ChatRoom testChatRoom = createTestChatRoom();

        given(chatRoomRepository.findById(any()))
                .willReturn(Optional.of(testChatRoom));

        given((chatRoomRepository.findByNameAndAndStudyId(any(),any())))
                .willReturn(Optional.empty());
        // when
        ChatRoomResponse result = chatRoomService.update(1L, TEST_CHAT_ROOM_UPDATE_REQUEST);

        // then
        assertThat(result.getName()).isEqualTo(TEST_CHAT_ROOM_UPDATE_REQUEST.getName());

        then(chatRoomRepository).should(times(1)).findById(any());
        then(chatRoomRepository).should(times(1)).findByNameAndAndStudyId(any(),any());
    }

    @Test
    @DisplayName("예외 테스트 : 스터디에 중복된 이름의 채팅방이 존재할 경우 스터디 수정에 실패한다.")
    void updateDuplicatedChatRoom() {
        // given
        ChatRoom testChatRoom = createTestChatRoom();

        given(chatRoomRepository.findById(any()))
                .willReturn(Optional.of(testChatRoom));

        given((chatRoomRepository.findByNameAndAndStudyId(any(), any())))
                .willReturn(Optional.of(TEST_CHAT_ROOM));
        // when
        assertThrows(ChatRoomException.class,
                () -> chatRoomService.update(1L, TEST_CHAT_ROOM_UPDATE_REQUEST));
    }

    @Test
    @DisplayName("채팅방을 삭제한다.")
    void deleteChatRoom(){
        // given
        given(chatRoomRepository.findById(any()))
                .willReturn(Optional.of(TEST_CHAT_ROOM));

        willDoNothing()
                .given(chatRoomRepository)
                .delete(any());

        willDoNothing()
                .given(chatMessageRepository)
                .deleteByChatRoomId(any());

        // when
        chatRoomService.delete(1L);

        // then
        then(chatRoomRepository).should(times(1)).findById(any());
        then(chatRoomRepository).should(times(1)).delete(any());
        then(chatMessageRepository).should(times(1)).deleteByChatRoomId(any());
    }
}