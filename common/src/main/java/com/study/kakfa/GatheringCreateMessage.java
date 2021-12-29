package com.study.kakfa;

import com.fasterxml.jackson.annotation.JsonFormat;
import jdk.vm.ci.meta.Local;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GatheringCreateMessage {

    private Long studyId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime gatheringTime;

    private boolean shape;

    private String content;

    public static GatheringCreateMessage from(Long studyId, LocalDateTime gatheringTime, boolean offline, String content) {
        return new GatheringCreateMessage(studyId, gatheringTime, offline, content);
    }

}
