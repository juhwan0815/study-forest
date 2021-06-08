package com.study.locationservice.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LocationSearchRequest {

    @NotBlank
    private String searchName; // 문자 검색용

}
