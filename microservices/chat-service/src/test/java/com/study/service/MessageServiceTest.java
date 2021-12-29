package com.study.service;

import com.study.MessageFixture;
import com.study.domain.Message;
import com.study.dto.MessageResponse;
import com.study.kafka.sender.MessageCreateSender;
import com.study.kakfa.ChatRoomDeleteMessage;
import com.study.kakfa.StudyDeleteMessage;
import com.study.repository.ChatRoomRepository;
import com.study.repository.MessageRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;

import static com.study.MessageFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @InjectMocks
    private MessageServiceImpl messageService;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessageCreateSender messageCreateSender;

    @Test
    @DisplayName("채팅방 메세지를 조회한다.")
    void findByRoomId() {
        // given
        Slice<Message> messages = new SliceImpl<>(Arrays.asList(TEST_MESSAGE), PageRequest.of(0, 50), true);

        given(messageRepository.findByRoomIdAndCreatedAtBeforeOrderByIdDesc(any(), any(), any()))
                .willReturn(messages);

        // when
        Slice<MessageResponse> result = messageService.findByRoomId(1L, PageRequest.of(0, 50), LocalDateTime.now().toString());

        // then
        assertThat(result.getContent().size()).isEqualTo(1);
        assertThat(result.isLast()).isFalse();
        then(messageRepository).should(times(1)).findByRoomIdAndCreatedAtBeforeOrderByIdDesc(any(), any(), any());
    }

    @Test
    @DisplayName("메세지를 전송한다.")
    void sendMessage() {
        // given
        given(messageRepository.save(any()))
                .willReturn(null);

        given(chatRoomRepository.getChatRoomUsers(any()))
                .willReturn(Arrays.asList(1L));

        willDoNothing()
                .given(messageCreateSender)
                .send(any());
        // when
        MessageResponse result = messageService.sendMessage(1L, "황주환", TEST_MESSAGE_REQUEST);

        // then
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getSender()).isEqualTo("황주환");
        assertThat(result.getContent()).isEqualTo(TEST_MESSAGE_REQUEST.getContent());
        assertThat(result.getCreatedAt()).isBefore(LocalDateTime.now());
    }

    @Test
    @DisplayName("스터디 삭제로 인한 채팅방 메세지를 삭제한다.")
    void deleteMessageByStudyDelete() {
        // given
        willDoNothing()
                .given(chatRoomRepository)
                .deleteChatRoom(any());

        willDoNothing()
                .given(messageRepository)
                .deleteByRoomIds(any());
        // when
        messageService.deleteMessage(StudyDeleteMessage.from(1L, Arrays.asList(1L)));

        // then
        then(chatRoomRepository).should(times(1)).deleteChatRoom(any());
        then(messageRepository).should(times(1)).deleteByRoomIds(any());
    }

    @Test
    @DisplayName("채팅방 삭제로 인한 채팅방 메세지를 삭제한다.")
    void deleteMessageByChatRoomDelete() {
        // given
        willDoNothing()
                .given(messageRepository)
                .deleteByRoomId(any());
        // when
        messageService.deleteMessage(ChatRoomDeleteMessage.from(1L));

        // then
        then(messageRepository).should(times(1)).deleteByRoomId(any());
    }
}