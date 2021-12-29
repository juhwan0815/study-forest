package com.study.repository;

import com.netflix.discovery.converters.Auto;
import com.study.domain.Message;
import com.study.dto.MessageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class MessageRepositoryTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private MessageRepository messageRepository;

    @BeforeEach
    void setUp() {
        messageRepository.deleteAll();
    }

    @Test
    @DisplayName("")
    void findByRoomIdAndCreatedAtBeforeOrOrderByIdDesc() {
        // given
        Message message = new Message(1L, 1L, "황주환", "안녕하세요", 1L, LocalDateTime.now());
        messageRepository.save(message);

        em.flush();
        em.clear();

        // when
        Slice<Message> result = messageRepository.findByRoomIdAndCreatedAtBeforeOrderByIdDesc(PageRequest.of(0, 10), 1L, LocalDateTime.now());

        // then
        assertThat(result.getContent().size()).isEqualTo(1);
        assertThat(result.isLast()).isTrue();
    }

    @Test
    @DisplayName("채팅방 ID 리스트로 메세지를 삭제한다.")
    void deleteByRoomIds() {
        // given
        Message message = Message.createMessage(1L, "황주환", "안녕하세요", 1L);
        messageRepository.save(message);

        em.flush();
        em.clear();

        // when
        messageRepository.deleteByRoomIds(Arrays.asList(1L));

        // then
        List<Message> result = messageRepository.findAll();
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("채팅방 ID 로 메세지를 삭제한다.")
    void deleteByRoomId() {
        // given
        Message message = Message.createMessage(1L, "황주환", "안녕하세요", 1L);
        messageRepository.save(message);

        em.flush();
        em.clear();

        // when
        messageRepository.deleteByRoomId(1L);

        // then
        List<Message> result = messageRepository.findAll();
        assertThat(result.size()).isEqualTo(0);
    }
}