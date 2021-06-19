package com.study.studyservice.kafka.sender;

import com.study.studyservice.kafka.message.StudyDeleteMessage;

public interface StudyDeleteMessageSender {

    void send(StudyDeleteMessage studyDeleteMessage);
}
