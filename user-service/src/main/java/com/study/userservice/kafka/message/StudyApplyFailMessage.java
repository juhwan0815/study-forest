package com.study.userservice.kafka.message;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyApplyFailMessage {

    private Long userId;

    private Long studyId;

    public static StudyApplyFailMessage from(Long userId,Long studyId){
        StudyApplyFailMessage studyApplyFailMessage = new StudyApplyFailMessage();
        studyApplyFailMessage.userId = userId;
        studyApplyFailMessage.studyId = studyId;
        return studyApplyFailMessage;
    }
}
