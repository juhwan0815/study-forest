package com.study.chatservice.repository;

import com.study.chatservice.domain.ChatRoom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ChatRoomRepositoryTest {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("스터디 ID와 이름으로 채팅방을 조회한다.")
    void findByNameAndStudyId(){
        // given
        ChatRoom chatRoom = ChatRoom.createChatRoom("공지사항", 1L);
        chatRoomRepository.save(chatRoom);

        em.flush();
        em.clear();

        // when
        ChatRoom result = chatRoomRepository.findByNameAndAndStudyId("공지사항", 1L).get();

        // then
        assertThat(result.getId()).isEqualTo(chatRoom.getId());
    }

    @Test
    @DisplayName("스터디 ID로 채팅방을 조회한다.")
    void findByStudyId(){
        // given
        ChatRoom chatRoom1 = ChatRoom.createChatRoom("공지사항", 1L);
        ChatRoom chatRoom2 = ChatRoom.createChatRoom("게임", 1L);

        chatRoomRepository.save(chatRoom1);
        chatRoomRepository.save(chatRoom2);

        em.flush();
        em.clear();

        // when
        List<ChatRoom> result = chatRoomRepository.findByStudyId(1L);

        // then
        assertThat(result.size()).isEqualTo(2);
    }

}