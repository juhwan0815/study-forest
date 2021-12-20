package com.study.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "area_id")
    private Long id;

    private String code; // 동 코드

    private String city; // 특별시, 광역시, 도

    private String gu; // 시, 군, 구

    private String dong; // 읍, 면, 동

    private String ri; // 리

    private Double let; // 위도

    private Double len; // 경도

    private String codeType; // 행정동/법정동

    public static Area createArea(String code, String city, String gu, String dong, String ri,
                                  Double let, Double len, String codeType) {
        Area area = new Area();
        area.code = code;
        area.city = city;
        area.gu = gu;
        area.dong = dong;
        area.ri = ri;
        area.let = let;
        area.len = len;
        area.codeType = codeType;
        return area;
    }
}
