package com.study.kakfa.sender;


import com.study.kakfa.StudyDeleteMessage;

public interface StudyDeleteMessageSender {

    void send(StudyDeleteMessage studyDeleteMessage);
}
