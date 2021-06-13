package com.study.studyservice.kafka.message;

import com.study.studyservice.domain.Study;
import lombok.Data;

@Data
public class StudyDeleteMessage {

    private Long studyId;

    public static StudyDeleteMessage from(Study study){
        StudyDeleteMessage studyDeleteMessage = new StudyDeleteMessage();
        studyDeleteMessage.studyId = study.getId();
        return studyDeleteMessage;
    }
}
