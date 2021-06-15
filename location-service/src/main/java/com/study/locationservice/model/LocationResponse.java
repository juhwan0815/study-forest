package com.study.locationservice.model;

import com.study.locationservice.domain.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationResponse {

    private Long id; // 지역 ID

    private String code; // 동 코드

    private String city; // 특별시,광역시,도

    private String gu; // 시,군,구

    private String dong; // 읍,면,동

    private String ri; // 리

    private Double let; // 위도

    private Double len; // 경도

    private String codeType; // 법정동, 행정동

    public static LocationResponse from(Location location){
        LocationResponse locationResponse = new LocationResponse();
        locationResponse.id = location.getId();
        locationResponse.code = location.getCode();
        locationResponse.city = location.getCity();
        locationResponse.gu = location.getGu();
        locationResponse.dong = location.getDong();
        locationResponse.ri = location.getRi();
        locationResponse.let = location.getLet();
        locationResponse.len = location.getLen();
        locationResponse.codeType = location.getCodeType();
        return locationResponse;
    }
}
