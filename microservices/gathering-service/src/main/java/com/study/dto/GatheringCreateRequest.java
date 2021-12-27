package com.study.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GatheringCreateRequest {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    @NotNull(message = "모임 시간은 필수입니다.")
    private LocalDateTime gatheringTime; // 모임 시간

    @NotNull(message = "온라인/오프라인 여부는 필수입니다.")
    private Boolean offline; // 온라인/오프라인 여부

    @NotBlank(message = "모임 내용은 필수입니다.")
    private String content; // 내용

    private String placeName; // 장소 이름

    private Double let; // 좌표 위도

    private Double len; // 좌표 경도
}
