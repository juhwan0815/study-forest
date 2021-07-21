package com.study.studyservice.model.study.response;

import com.study.studyservice.domain.Study;
import com.study.studyservice.model.studyuser.StudyUserResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyWithUserResponse {

    private String studyName;

    private List<StudyUserResponse> studyUsers;

    public static StudyWithUserResponse from(Study study,List<StudyUserResponse> studyUsers) {
        StudyWithUserResponse studyWithUserResponse = new StudyWithUserResponse();
        studyWithUserResponse.studyName = study.getName();
        studyWithUserResponse.studyUsers = studyUsers;
        return studyWithUserResponse;
    }
}
