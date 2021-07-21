package com.study.gatheringservice.kafka.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.gatheringservice.domain.Gathering;
import com.study.gatheringservice.domain.Shape;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GatheringCreateMessage implements Serializable {

    private Long studyId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime gatheringTime;

    private String shape;

    private String content;

    public static GatheringCreateMessage from(Gathering gathering) {
        GatheringCreateMessage gatheringCreateMessage = new GatheringCreateMessage();
        gatheringCreateMessage.studyId = gathering.getStudyId();
        gatheringCreateMessage.gatheringTime = gathering.getGatheringTime();
        if (gathering.getShape().equals(Shape.OFFLINE)) {
            gatheringCreateMessage.shape = "오프라인";
        } else {
            gatheringCreateMessage.shape = "온라인";
        }
        gatheringCreateMessage.content = gathering.getContent();
        return gatheringCreateMessage;
    }

}
