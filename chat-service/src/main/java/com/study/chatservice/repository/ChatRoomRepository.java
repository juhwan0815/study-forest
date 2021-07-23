package com.study.chatservice.repository;

import com.study.chatservice.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom,Long> {

    Optional<ChatRoom> findByNameAndAndStudyId(String name,Long studyId);

    List<ChatRoom> findByStudyId(Long studyId);
}
