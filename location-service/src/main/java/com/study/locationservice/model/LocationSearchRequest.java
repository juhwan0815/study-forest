package com.study.locationservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationSearchRequest {

    @NotBlank
    private String searchName; // 문자 검색용

}
