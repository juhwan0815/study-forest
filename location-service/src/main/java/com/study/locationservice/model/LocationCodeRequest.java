package com.study.locationservice.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LocationCodeRequest {

    @NotBlank
    private String code;
}
