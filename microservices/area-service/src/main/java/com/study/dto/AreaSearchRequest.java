package com.study.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AreaSearchRequest {

    @NotBlank(message = "검색어는 필수입니다.")
    private String searchWord;

    private Long areaId;

    @NotNull(message = "사이즈는 필수입니다.")
    @Positive(message = "사이즈는 양수이어야 합니다.")
    private Integer size;
}
