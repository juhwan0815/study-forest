package com.study.studyservice.model.studytag;

import com.study.studyservice.domain.StudyTag;
import lombok.Data;

@Data
public class StudyTagResponse {

    private Long id;

    private String tagName;

    public static StudyTagResponse from(StudyTag studyTag){
        StudyTagResponse studyTagResponse = new StudyTagResponse();
        studyTagResponse.id = studyTag.getId();
        studyTagResponse.tagName = studyTag.getTag().getName();
        return studyTagResponse;
    }
}
