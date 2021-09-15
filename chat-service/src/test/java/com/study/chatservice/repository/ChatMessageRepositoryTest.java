package com.study.chatservice.repository;

import com.study.chatservice.domain.ChatMessage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManager;

import java.util.stream.LongStream;

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
    void deleteByChatRoomId() {
        // given
        ChatMessage message1 = ChatMessage.createMessage(1L,"황주환", "안녕하세요", 1L);
        ChatMessage message2 = ChatMessage.createMessage(1L,"황주환", "안녕하세요", 1L);
        chatMessageRepository.save(message1);
        chatMessageRepository.save(message2);

        em.flush();
        em.clear();

        // when
        chatMessageRepository.deleteByChatRoomId(1L);

        // then
        assertThat(chatMessageRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("채팅방 ID로 채팅을 페이징 조회한다.")
    void findByChatRoomIdOrderByCreatedAtDesc() {
        // given
        LongStream.range(0, 20).forEach(value -> {
            ChatMessage message = ChatMessage.createMessage(1L,"황주환", "안녕하세요", 1L);
            chatMessageRepository.save(message);
        });

        em.flush();
        em.clear();

        // when
        Page<ChatMessage> result = chatMessageRepository.findByChatRoomIdOrderByCreatedAtDesc(
                PageRequest.of(0,5), 1L);

        // then
        assertThat(result.getContent().size()).isEqualTo(5);
        assertThat(result.getTotalElements()).isEqualTo(20);
        assertThat(result.getTotalPages()).isEqualTo(4);
    }

}