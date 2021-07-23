package com.study.chatservice.repository;

import com.study.chatservice.domain.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long> {

    @Modifying(clearAutomatically= true)
    @Query("delete from ChatMessage cm where cm.chatRoomId =:chatRoomId")
    void deleteByChatRoomId(@Param("chatRoomId") Long chatRoomId);

    Page<ChatMessage> findByChatRoomIdOrderByCreatedAtDesc(Pageable pageable,Long chatRoomId);
}
