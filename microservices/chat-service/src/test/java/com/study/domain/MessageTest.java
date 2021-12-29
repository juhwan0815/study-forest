package com.study.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    @Test
    @DisplayName("메세지를 생성한다.")
    void createMessage() {
        // given
        Long userId = 1L;
        String sender = "황주환";
        String content = "안녕하세요";
        Long roomId = 1L;

        // when
        Message result = Message.createMessage(userId, sender, content, roomId);

        // then
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getSender()).isEqualTo(sender);
        assertThat(result.getContent()).isEqualTo(content);
        assertThat(result.getRoomId()).isEqualTo(roomId);
        assertThat(result.getCreatedAt()).isBefore(LocalDateTime.now());
    }
}