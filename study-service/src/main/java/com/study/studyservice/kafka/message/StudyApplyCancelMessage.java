package com.study.studyservice.kafka.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyApplyCancelMessage {

    private Long userId;

    private Long studyId;

    public static StudyApplyCancelMessage from(Long userId,Long studyId){
        StudyApplyCancelMessage studyApplyCancelMessage = new StudyApplyCancelMessage();
        studyApplyCancelMessage.userId = userId;
        studyApplyCancelMessage.studyId = studyId;
        return studyApplyCancelMessage;
    }
}
