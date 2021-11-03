package com.study.chatservice.repository;

import com.study.chatservice.domain.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long> {

    @Modifying(clearAutomatically= true)
    @Query("delete from ChatMessage cm where cm.chatRoomId =:chatRoomId")
    void deleteByChatRoomId(@Param("chatRoomId") Long chatRoomId);

    Page<ChatMessage> findByChatRoomIdAndCreatedAtBeforeOrderByCreatedAtDesc(Pageable pageable,
                                                                             Long chatRoomId,
                                                                             LocalDateTime lastMessageDate);
}