package com.study.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChatRoomTest {

    @Test
    @DisplayName("스터디 채팅방을 생성한다.")
    void createChatRoom() {
        // given
        String name = "공지사항";

        // when
        ChatRoom result = ChatRoom.createChatRoom(name, null);

        // then
        assertThat(result.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("스터디 채팅방 이름을 수정한다.")
    void changeName() {
        // given
        ChatRoom chatRoom = ChatRoom.createChatRoom(null, null);

        // when
        chatRoom.changeName("공지사항");

        // then
        assertThat(chatRoom.getName()).isEqualTo("공지사항");
    }
}