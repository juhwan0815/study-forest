package com.study.kakfa.sender;


import com.study.kakfa.StudyCreateMessage;

public interface StudyCreateMessageSender {

    void send(StudyCreateMessage studyCreateMessage);
}
