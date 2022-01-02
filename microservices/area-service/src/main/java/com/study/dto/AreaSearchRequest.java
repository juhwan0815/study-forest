package com.study.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AreaSearchRequest {

    @NotBlank(message = "검색어는 필수입니다.")
    private String searchWord;

}
