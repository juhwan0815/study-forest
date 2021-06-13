package com.study.studyservice.kafka.sender;

import com.study.studyservice.kafka.message.StudyJoinMessage;

public interface KafkaStudyJoinMessageSender {

    void send(StudyJoinMessage studyJoinMessage);
}
