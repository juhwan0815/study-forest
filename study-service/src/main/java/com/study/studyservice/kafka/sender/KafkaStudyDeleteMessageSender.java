package com.study.studyservice.kafka.sender;

import com.study.studyservice.kafka.message.StudyDeleteMessage;

public interface KafkaStudyDeleteMessageSender {

    void send(StudyDeleteMessage studyDeleteMessage);
}
