package com.study.locationservice;

import com.study.locationservice.domain.Location;
import com.study.locationservice.model.LocationCodeRequest;
import com.study.locationservice.model.LocationCreateRequest;
import com.study.locationservice.model.LocationResponse;
import com.study.locationservice.model.LocationSearchRequest;

public class LocationFixture {

    public static final LocationSearchRequest TEST_LOCATION_SEARCH_REQUEST
            = new LocationSearchRequest("삼청동");

    public static final LocationCreateRequest TEST_LOCATION_CREATE_REQUEST1
            = new LocationCreateRequest("1111054000","서울특별시","종로구","삼청동",
                                        "--리",37.590758,126.980996,"H");

    public static final LocationCreateRequest TEST_LOCATION_CREATE_REQUEST2
            = new LocationCreateRequest("1111051500","서울특별시","종로구","청운효자동",
                                    "--리",37.584009,126.970626,"H");

    public static final LocationCodeRequest TEST_LOCATION_CODE_REQUEST
            = new LocationCodeRequest("1111051500");

    public static final Location TEST_LOCATION
            = new Location(1L,"1111054000","서울특별시","종로구","삼청동",
                        null, 37.590758, 126.980996, "H");

    public static final Location TEST_LOCATION2
            = new Location(1L,"1111054000","서울특별시","종로구","청운효자동",
            null, 37.584009, 126.970626, "H");

    public static final LocationResponse TEST_LOCATION_RESPONSE1 =
            new LocationResponse(1L,"1111054000","서울특별시","종로구","삼청동",
                                "--리", 37.590758, 126.980996, "H");

    public static final LocationResponse TEST_LOCATION_RESPONSE2 =
            new LocationResponse(2L,"1111051500","서울특별시","종로구","청운효자동",
                    "--리", 37.584009, 126.970626, "H");
}
