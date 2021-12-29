package com.study.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ChatRoomRepositoryTest {

    @Test
    @DisplayName("처음 채팅방에 회원을 추가한다.")
    void addChatRoomUserNotExistRoom() {
        // given
        ChatRoomRepository chatRoomRepository = new ChatRoomRepository();

        // when
        chatRoomRepository.addChatRoomUser(1L, "content", 1L);

        // then
        assertThat(chatRoomRepository.getChatRoomUsers().size()).isEqualTo(1);
        assertThat(chatRoomRepository.getChatRoomUsers().get(1L).size()).isEqualTo(1);
    }

    @Test
    @DisplayName("채팅방에 회원을 추가한다.")
    void addChatRoomUserExistRoom() {
        // given
        ChatRoomRepository chatRoomRepository = new ChatRoomRepository();
        chatRoomRepository.addChatRoomUser(1L, "content1", 1L);

        // when
        chatRoomRepository.addChatRoomUser(1L, "content2", 2L);

        // then
        assertThat(chatRoomRepository.getChatRoomUsers().size()).isEqualTo(1);
        assertThat(chatRoomRepository.getChatRoomUsers().get(1L).size()).isEqualTo(2);
    }

    @Test
    @DisplayName("채팅방에서 회원을 삭제한다.")
    void deleteChatRoomUser() {
        // given
        ChatRoomRepository chatRoomRepository = new ChatRoomRepository();
        chatRoomRepository.addChatRoomUser(1L, "content", 1L);

        // when
        chatRoomRepository.deleteChatRoomUser(1L, "content");

        // then
        assertThat(chatRoomRepository.getChatRoomUsers().size()).isEqualTo(1);
        assertThat(chatRoomRepository.getChatRoomUsers().get(1L).size()).isEqualTo(0);
    }


    @Test
    @DisplayName("모든 채팅방을 삭제한다.")
    void deleteChatRoom() {
        // given
        ChatRoomRepository chatRoomRepository = new ChatRoomRepository();
        chatRoomRepository.addChatRoomUser(1L, "content", 1L);

        // when
        chatRoomRepository.deleteChatRoom(Arrays.asList(1L));

        // then
        assertThat(chatRoomRepository.getChatRoomUsers().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("회원이 입장한 채팅방를 추가한다,")
    void addSessionRoom() {
        // given
        ChatRoomRepository chatRoomRepository = new ChatRoomRepository();

        // when
        chatRoomRepository.addSessionRoom("content",1L);

        // then
        assertThat(chatRoomRepository.getSessionRooms().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("회원이 입장한 채팅방을 삭제한다.")
    void deleteSessionRoom() {
        // given
        ChatRoomRepository chatRoomRepository = new ChatRoomRepository();
        chatRoomRepository.addSessionRoom("content",1L);

        // when
        chatRoomRepository.deleteSessionRoom("content");

        // then
        assertThat(chatRoomRepository.getSessionRooms().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("회원이 입장한 채팅방 ID 를 조회한다.")
    void getRoomBySession() {
        // given
        ChatRoomRepository chatRoomRepository = new ChatRoomRepository();
        chatRoomRepository.addSessionRoom("content",1L);

        // when
        Long roomId = chatRoomRepository.getRoomBySession("content");

        // then
        assertThat(roomId).isEqualTo(1L);
    }

    @Test
    @DisplayName("채팅방에 입장한 회원 ID 리스트를 반환한다.")
    void getChatRoomUsers() {
        // given
        ChatRoomRepository chatRoomRepository = new ChatRoomRepository();
        chatRoomRepository.addChatRoomUser(1L, "content", 1L);

        // when
        List<Long> userIds = chatRoomRepository.getChatRoomUsers(1L);

        // then
        assertThat(userIds.size()).isEqualTo(1);
        assertThat(userIds.get(0)).isEqualTo(1L);
    }
}