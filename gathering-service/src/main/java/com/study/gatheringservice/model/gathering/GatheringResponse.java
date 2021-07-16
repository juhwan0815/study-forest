package com.study.gatheringservice.model.gathering;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.study.gatheringservice.domain.Gathering;
import com.study.gatheringservice.domain.GatheringUser;
import com.study.gatheringservice.domain.Place;
import com.study.gatheringservice.domain.Shape;
import com.study.gatheringservice.model.gatheringuser.GatheringUserResponse;
import com.study.gatheringservice.model.studyuser.StudyUserResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GatheringResponse {

    private Long id;

    private Long studyId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime gatheringTime;

    private Integer numberOfPeople;

    private Shape shape;

    private String content;

    private Place place;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Boolean apply;

    public static GatheringResponse from(Gathering gathering){
        GatheringResponse gatheringResponse = new GatheringResponse();
        gatheringResponse.id = gathering.getId();
        gatheringResponse.studyId = gathering.getStudyId();
        gatheringResponse.gatheringTime = gathering.getGatheringTime();
        gatheringResponse.numberOfPeople = gathering.getNumberOfPeople();
        gatheringResponse.shape = gathering.getShape();
        gatheringResponse.content = gathering.getContent();
        gatheringResponse.place = gathering.getPlace();
        return gatheringResponse;
    }
}
