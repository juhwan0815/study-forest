package com.study;

import com.study.domain.Area;
import com.study.dto.*;

public class AreaFixture {

    public static final AreaCreateRequest TEST_AREA_CREATE_REQUEST
            = new AreaCreateRequest("1111054000", "서울특별시", "종로구", "삼청동", "--리", 37.590758, 126.980996, "H");

    public static final AreaAroundRequest TEST_AREA_AROUND_REQUEST
            = new AreaAroundRequest(3);

    public static final AreaCodeRequest TEST_AREA_CODE_REQUEST
            = new AreaCodeRequest("1111054000");

    public static final AreaSearchRequest TEST_AREA_SEARCH_REQUEST
            = new AreaSearchRequest("삼청동");

    public static final AreaResponse TEST_AREA_RESPONSE
            = new AreaResponse(1L, "1111054000", "서울특별시", "종로구", "삼청동", "--리", 37.590758, 126.980996, "H");

    public static final Area TEST_AREA
            = Area.createArea("1111054000", "서울특별시", "종로구", "삼청동", "--리", 37.590758, 126.980996, "H");

}
