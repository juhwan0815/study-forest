package com.study.repository;

import com.study.domain.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Slice<Message> findByRoomIdAndCreatedAtBeforeOrOrderByIdDesc(Pageable pageable, Long roomId, LocalDateTime lastMessageDate);
}
