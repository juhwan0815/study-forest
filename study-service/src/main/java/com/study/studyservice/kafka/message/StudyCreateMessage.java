package com.study.studyservice.kafka.message;

import com.study.studyservice.domain.Study;
import com.study.studyservice.domain.Tag;
import com.study.studyservice.model.tag.TagResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyCreateMessage {

    private Long studyId;

    private String studyName;

    private List<TagResponse> tags;

    public static StudyCreateMessage from(Study study){
        StudyCreateMessage studyCreateMessage = new StudyCreateMessage();
        studyCreateMessage.studyId = study.getId();
        studyCreateMessage.studyName = study.getName();
        studyCreateMessage.tags = study.getStudyTags().stream()
                .map(studyTag -> TagResponse.from(studyTag.getTag()))
                .collect(Collectors.toList());
        return studyCreateMessage;
    }
}
