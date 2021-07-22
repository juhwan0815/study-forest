package com.study.studyservice.kafka.sender;

import com.study.studyservice.kafka.message.StudyCreateMessage;

public interface StudyCreateMessageSender {

    void send(StudyCreateMessage studyCreateMessage);
}
