package com.study.gathering;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Place {

    private String name; // 장소 이름

    private Double let; // 좌표 위도

    private Double len; // 좌표 경도

    public static Place createPlace(String name, Double let, Double len) {
        Place place = new Place();
        place.name = name;
        place.let = let;
        place.len = len;
        return place;
    }
}
