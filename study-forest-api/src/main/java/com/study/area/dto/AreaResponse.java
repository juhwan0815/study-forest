package com.study.area.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.study.area.Area;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AreaResponse {

    private Long areaId;

    private String code;

    private String city;

    private String gu;

    private String dong;

    private String ri;

    private Double let;

    private Double len;

    private String codeType;

    @QueryProjection
    public AreaResponse(Area area) {
        this.areaId = area.getId();
        this.code = area.getCode();
        this.city = area.getCity();
        this.gu = area.getGu();
        this.dong = area.getDong();
        this.ri = area.getRi();
        this.let = area.getLet();
        this.len = area.getLen();
        this.codeType = area.getCodeType();
    }

}
