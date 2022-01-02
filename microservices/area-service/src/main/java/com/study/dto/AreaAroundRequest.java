package com.study.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AreaAroundRequest {

    @NotNull(message = "거리는 필수입니다.")
    @Min(value = 3, message = "거리는 3이상이어야 합니다.")
    @Max(value = 12, message = "거리는 12이하이어야 합니다.")
    private Integer distance;
}
