package com.study.repository;

import com.study.domain.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Slice<Message> findByRoomIdAndCreatedAtBeforeOrderByIdDesc(Pageable pageable, Long roomId, LocalDateTime lastMessageDate);

    @Modifying(clearAutomatically= false)
    @Query("delete from Message m where m.roomId in :roomIds")
    void deleteByRoomIds(@Param("roomIds") List<Long> roomIds);

    @Modifying(clearAutomatically= false)
    @Query("delete from Message m where m.roomId =:roomId")
    void deleteByRoomId(@Param("roomId") Long roomId);
}
