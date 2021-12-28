package com.study.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.domain.Gathering;
import com.study.domain.Place;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GatheringResponse {

    private Long gatheringId;

    private Long studyId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime gatheringTime;

    private Integer numberOfPeople;

    private Boolean offline;

    private String content;

    private Place place;

    public static GatheringResponse from(Gathering gathering) {
        GatheringResponse gatheringResponse = new GatheringResponse();
        gatheringResponse.gatheringId = gathering.getId();
        gatheringResponse.studyId = gathering.getStudyId();
        gatheringResponse.gatheringTime = gathering.getGatheringTime();
        gatheringResponse.numberOfPeople = gathering.getNumberOfPeople();
        gatheringResponse.offline = gathering.isOffline();
        gatheringResponse.content = gathering.getContent();
        gatheringResponse.place = gathering.getPlace();
        return gatheringResponse;
    }
}
