package com.study.studyservice.kafka.message;

import lombok.Data;

@Data
public class StudyJoinMessage {

    private Long userId;

    private Long studyId;

    private boolean create;

    private boolean fail;

    private boolean success;

    public static StudyJoinMessage createStudyJoin(Long userId,Long studyId){
        StudyJoinMessage studyJoinMessage = new StudyJoinMessage();
        studyJoinMessage.userId = userId;
        studyJoinMessage.studyId = studyId;
        studyJoinMessage.create = true;
        return studyJoinMessage;
    }
}
