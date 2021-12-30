package com.study.kakfa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyCreateMessage {

    private Long studyId;

    private String name;

    private List<String> tags;

    public static StudyCreateMessage from(Long studyId, String name, List<String> tags) {
        return new StudyCreateMessage(studyId, name, tags);
    }
}
