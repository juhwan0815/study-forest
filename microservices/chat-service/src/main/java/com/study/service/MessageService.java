package com.study.service;

import com.study.domain.Message;
import com.study.dto.MessageRequest;
import com.study.dto.MessageResponse;
import com.study.kakfa.StudyDeleteMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MessageService {

    Slice<MessageResponse> findByRoomId(Long roomId, Pageable pageable, String lastMessageDate);

    MessageResponse sendMessage(Long userId, String sender, MessageRequest request);

    void deleteMessage(StudyDeleteMessage studyDeleteMessage);
}
