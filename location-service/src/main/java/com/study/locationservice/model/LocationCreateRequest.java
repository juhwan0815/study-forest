package com.study.locationservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationCreateRequest {

    @NotBlank(message = "지역코드는 필수입니다.")
    private String code; // 동 코드

    @NotBlank(message = "특별시/광역시/도는 필수입니다.")
    private String city; // 특별시,광역시,도

    @NotBlank(message = "시/군/구는 필수입니다.")
    private String gu; // 시,군,구

    @NotBlank(message = "읍/면/동는 필수입니다.")
    private String dong; // 읍,면,동

    @NotBlank(message = "리는 필수입니다.")
    private String ri; // 리

    private Double let; // 위도

    private Double len; // 경도

    @NotBlank(message = "코드 타입은 필수입니다.")
    private String codeType; // ?
}
