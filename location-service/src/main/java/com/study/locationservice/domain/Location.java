package com.study.locationservice.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code; // 동 코드

    private String city; // 특별시,광역시,도

    private String gu; // 시,군,구

    private String dong; // 읍,면,동

    private String ri; // 리

    private Double let; // 위도

    private Double len; // 경도

    private String codeType; // 행정동 법정동

    public static Location createLocation(String code, String city, String gu, String dong, String ri, Double let, Double len, String codeType) {
        Location location = new Location();
        location.code = code;
        location.city = city;
        location.gu = gu;
        location.dong = dong;
        location.ri = ri;
        location.let = let;
        location.len = len;
        location.codeType = codeType;
        return location;
    }
}
