package com.study;

import com.study.domain.Area;
import com.study.dto.*;

public class AreaFixture {

    public static final String TEST_AREA_CODE = "1111054000";
    public static final String TEST_AREA_CITY = "서울특별시";
    public static final String TEST_AREA_GU = "종로구";
    public static final String TEST_AREA_DONG = "삼청동";
    public static final String TEST_AREA_RI = "--리";
    public static final Double TEST_AREA_LET = 37.590758;
    public static final Double TEST_AREA_LEN = 126.980996;
    public static final String TEST_AREA_CODE_TYPE = "H";

    public static final String TEST_AUTHORIZATION = "bearer *****";
    public static final Integer TEST_SIZE = 10;
    public static final Integer TEST_DISTANCE = 3;

    public static final Area TEST_AREA
            = new Area(1L, TEST_AREA_CODE, TEST_AREA_CITY, TEST_AREA_GU, TEST_AREA_DONG, TEST_AREA_RI, TEST_AREA_LET, TEST_AREA_LEN, TEST_AREA_CODE_TYPE);

    public static final AreaCreateRequest TEST_AREA_CREATE_REQUEST
            = new AreaCreateRequest(TEST_AREA_CODE, TEST_AREA_CITY, TEST_AREA_GU, TEST_AREA_DONG, TEST_AREA_RI, TEST_AREA_LET, TEST_AREA_LEN, TEST_AREA_CODE_TYPE);

    public static final AreaAroundRequest TEST_AREA_AROUND_REQUEST
            = new AreaAroundRequest(TEST_DISTANCE);

    public static final AreaCodeRequest TEST_AREA_CODE_REQUEST
            = new AreaCodeRequest(TEST_AREA_CODE);

    public static final AreaSearchRequest TEST_AREA_SEARCH_REQUEST
            = new AreaSearchRequest(TEST_AREA_DONG, TEST_AREA.getId(), TEST_SIZE);

    public static final AreaResponse TEST_AREA_RESPONSE
            = new AreaResponse(TEST_AREA.getId(), TEST_AREA_CODE, TEST_AREA_CITY, TEST_AREA_GU, TEST_AREA_DONG, TEST_AREA_RI, TEST_AREA_LET, TEST_AREA_LEN, TEST_AREA_CODE_TYPE);


}
