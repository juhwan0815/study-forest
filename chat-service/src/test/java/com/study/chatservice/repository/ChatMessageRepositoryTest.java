package com.study.chatservice.repository;

import com.study.chatservice.domain.ChatMessage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ChatMessageRepositoryTest {


    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("채팅방 ID로 채팅을 모두 삭제한다.")
    void deleteByChatRoomId(){
        // given
        ChatMessage message1 = ChatMessage.createMessage("황주환", "안녕하세요", 1L);
        ChatMessage message2 = ChatMessage.createMessage("황주환", "안녕하세요", 1L);
        chatMessageRepository.save(message1);
        chatMessageRepository.save(message2);

        em.flush();
        em.clear();

        // when
        chatMessageRepository.deleteByChatRoomId(1L);

        // then
        assertThat(chatMessageRepository.findAll().size()).isEqualTo(0);
    }

}