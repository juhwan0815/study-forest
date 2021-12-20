package com.study.dto;

import com.study.domain.Area;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AreaResponse {

    private Long id;

    private String code;

    private String city;

    private String gu;

    private String dong;

    private String ri;

    private Double let;

    private Double len;

    private String codeType;

    public static AreaResponse from(Area area){
        return new AreaResponse(area.getId(), area.getCode(), area.getCity(), area.getGu(), area.getDong(),
                                area.getRi(), area.getLet(), area.getLen(), area.getCodeType());
    }
}
