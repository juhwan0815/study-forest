package com.study.studyservice.model.location.request;

import lombok.Data;

@Data
public class LocationCodeRequest {

    private String code;

    public static LocationCodeRequest from(String code){
        LocationCodeRequest locationCodeRequest = new LocationCodeRequest();
        locationCodeRequest.code = code;
        return locationCodeRequest;
    }
}
