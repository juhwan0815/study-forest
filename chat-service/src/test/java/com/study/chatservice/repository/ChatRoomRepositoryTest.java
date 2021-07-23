package com.study.chatservice.repository;

import com.study.chatservice.domain.ChatRoom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;

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
    @DisplayName("스터디 ID와 이름으로 채팅방으로 조회한다.")
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

}