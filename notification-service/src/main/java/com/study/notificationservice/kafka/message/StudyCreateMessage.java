package com.study.notificationservice.kafka.message;

import com.study.notificationservice.model.tag.TagResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyCreateMessage {

    private Long studyId;

    private String studyName;

    private List<TagResponse> tags;

}
