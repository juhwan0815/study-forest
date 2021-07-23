package com.study.chatservice.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class ChatRoomTest {

    @Test
    @DisplayName("채팅방의 이름을 변경한다.")
    void changeName(){
        // given
        ChatRoom chatRoom = ChatRoom.createChatRoom("공지사항", 1L);

        // when
        chatRoom.changeName("떠드는방");

        // then
        assertThat(chatRoom.getName()).isEqualTo("떠드는방");
    }


}