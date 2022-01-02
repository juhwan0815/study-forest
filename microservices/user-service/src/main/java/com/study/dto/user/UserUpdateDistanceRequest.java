package com.study.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDistanceRequest {

    @NotNull(message = "변경할 거리값은 필수입니다.")
    @Min(value = 3, message = "변경할 거리값은 3이상이어야 합니다.")
    @Max(value = 12, message = "변경할 거리값은 12이하이어야 합나다.")
    private Integer distance;
}
