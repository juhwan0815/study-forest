package com.study.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class AreaCodeRequest {

    @NotBlank(message = "지역 코드는 필수입니다.")
    private String code;
}
