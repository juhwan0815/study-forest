package com.study.kakfa;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyApplyFailMessage {

    private Long userId;

    private Long studyId;

    private String studyName;

    public static StudyApplyFailMessage from(Long userId, Long studyId, String studyName){
        return new StudyApplyFailMessage(userId, studyId, studyName);
    }
}
