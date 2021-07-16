package com.study.gatheringservice.model.gathering;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.gatheringservice.domain.Shape;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GatheringCreateRequest {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime gatheringTime; // 모임 시간

    @NotNull
    private Shape shape; // 온라인/오프라인 여부

    private String content; // 내용

    private String placeName; // 장소 이름

    private Double let; // 좌표 위도

    private Double len; // 좌표 경도
}
