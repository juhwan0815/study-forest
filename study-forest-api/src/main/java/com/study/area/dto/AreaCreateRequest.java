package com.study.area.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AreaCreateRequest {

    @NotBlank(message = "지역 코드는 필수입니다.")
    private String code; // 코드

    @NotBlank(message = "시는 필수입니다.")
    private String city; // 특별시, 광역시, 도

    @NotBlank(message = "구는 필수입니다.")
    private String gu; // 시, 군, 구

    @NotBlank(message = "동은 필수입니다.")
    private String dong; // 읍, 면, 동

    private String ri; // 리

    private Double let; // 위도

    private Double len; // 경도

    @NotBlank(message = "코드 타입은 필수입니다.")
    private String codeType; // 법정동/행정동 구분
}
