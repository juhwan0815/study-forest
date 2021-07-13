package com.study.locationservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.locationservice.LocationFixture;
import com.study.locationservice.model.LocationCodeRequest;
import com.study.locationservice.model.LocationCreateRequest;
import com.study.locationservice.model.LocationResponse;
import com.study.locationservice.service.LocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.study.locationservice.LocationFixture.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest
class LocationControllerTest {

    private final String TEST_AUTHORIZATION = "Bearer 액세스토큰";

    @MockBean
    private LocationService locationService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void init(WebApplicationContext wac,
              RestDocumentationContextProvider restDocumentationContextProvider) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .apply(documentationConfiguration(restDocumentationContextProvider)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();
    }

    @Test
    @DisplayName("지역정보(위치) 생성 API 테스트")
    void create() throws Exception {
        List<LocationCreateRequest> request = new ArrayList<>();
        request.add(TEST_LOCATION_CREATE_REQUEST1);
        request.add(TEST_LOCATION_CREATE_REQUEST2);

        willDoNothing()
                .given(locationService)
                .create(anyList());

        mockMvc.perform(post("/locations")
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("location/create",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                        ),
                        requestFields(
                                fieldWithPath("[]").type(JsonFieldType.ARRAY).description("지역 정보 목록"),
                                fieldWithPath("[].code").type(JsonFieldType.STRING).description("행정동/법정동 코드"),
                                fieldWithPath("[].city").type(JsonFieldType.STRING).description("특별시, 광역시, 도"),
                                fieldWithPath("[].gu").type(JsonFieldType.STRING).description("시, 군, 구"),
                                fieldWithPath("[].dong").type(JsonFieldType.STRING).description("읍, 면, 동"),
                                fieldWithPath("[].ri").type(JsonFieldType.STRING).description("리"),
                                fieldWithPath("[].let").type(JsonFieldType.NUMBER).description("위도"),
                                fieldWithPath("[].len").type(JsonFieldType.NUMBER).description("경도"),
                                fieldWithPath("[].codeType").type(JsonFieldType.STRING).description("코드 타입")
                        )));

        then(locationService).should(times(1)).create(anyList());
    }

    @Test
    @DisplayName("지역정보 ID로 조회 API 테스트")
    void findById() throws Exception {

        given(locationService.findById(anyLong()))
                .willReturn(TEST_LOCATION_RESPONSE1);

        mockMvc.perform(get("/locations/{locationId}", 1)
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_LOCATION_RESPONSE1)))
                .andDo(document("location/findById",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access 토큰")
                        ),
                        pathParameters(
                                parameterWithName("locationId").description("지역정보 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("지역정보 ID"),
                                fieldWithPath("code").type(JsonFieldType.STRING).description("행정동/법정동 코드"),
                                fieldWithPath("city").type(JsonFieldType.STRING).description("시/도"),
                                fieldWithPath("gu").type(JsonFieldType.STRING).description("시/군/구"),
                                fieldWithPath("dong").type(JsonFieldType.STRING).description("읍/면/동"),
                                fieldWithPath("ri").type(JsonFieldType.STRING).description("리"),
                                fieldWithPath("let").type(JsonFieldType.NUMBER).description("위도"),
                                fieldWithPath("len").type(JsonFieldType.NUMBER).description("경도"),
                                fieldWithPath("codeType").type(JsonFieldType.STRING).description("코드타입")
                        )));

        then(locationService).should(times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("지역정보 검색어 조회 API")
    void findBySearchName() throws Exception {
        List<LocationResponse> content = new ArrayList<>();
        content.add(TEST_LOCATION_RESPONSE1);
        content.add(TEST_LOCATION_RESPONSE2);
        PageRequest pageable = PageRequest.of(0, 20);
        Page<LocationResponse> pageLocations = new PageImpl<>(content, pageable, content.size());

        given(locationService.findBySearchCondition(any(), any()))
                .willReturn(pageLocations);

        mockMvc.perform(get("/locations/search")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                .param("page", "0")
                .param("size", "20")
                .param("searchName", "삼청동"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(pageLocations)))
                .andDo(document("location/findBySearchName",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        requestParameters(
                                parameterWithName("searchName").description("검색어"),
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("페이지 사이즈")
                        ),
                        responseFields(
                                fieldWithPath("content").type(JsonFieldType.ARRAY).description("조회 결과 배열"),
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("지역정보 ID"),
                                fieldWithPath("content.[].code").type(JsonFieldType.STRING).description("행정동/법정동 코드"),
                                fieldWithPath("content.[].city").type(JsonFieldType.STRING).description("시/도"),
                                fieldWithPath("content.[].gu").type(JsonFieldType.STRING).description("시/군/구"),
                                fieldWithPath("content.[].dong").type(JsonFieldType.STRING).description("읍/면/동"),
                                fieldWithPath("content.[].ri").type(JsonFieldType.STRING).description("리"),
                                fieldWithPath("content.[].let").type(JsonFieldType.NUMBER).description("위도"),
                                fieldWithPath("content.[].len").type(JsonFieldType.NUMBER).description("경도"),
                                fieldWithPath("content.[].codeType").type(JsonFieldType.STRING).description("코드타입"),
                                fieldWithPath("pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부 "),
                                fieldWithPath("pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"),
                                fieldWithPath("pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("값이 비었는지 여부"),
                                fieldWithPath("pageable.offset").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                fieldWithPath("pageable.pageNumber").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                fieldWithPath("pageable.pageSize").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                fieldWithPath("pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 여부"),
                                fieldWithPath("pageable.unpaged").type(JsonFieldType.BOOLEAN).description("비페이징 여부"),
                                fieldWithPath("last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                                fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("총 페이지 수"),
                                fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("총 결과 수"),
                                fieldWithPath("size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                fieldWithPath("number").type(JsonFieldType.NUMBER).description("페이지 번호"),
                                fieldWithPath("sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬여부"),
                                fieldWithPath("sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬여부"),
                                fieldWithPath("sort.empty").type(JsonFieldType.BOOLEAN).description("값이 비었는지 여부"),
                                fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지 크기"),
                                fieldWithPath("first").type(JsonFieldType.BOOLEAN).description("처음 페이지 여부"),
                                fieldWithPath("empty").type(JsonFieldType.BOOLEAN).description("값이 비었는지 여부")
                        )
                ));
        then(locationService).should(times(1)).findBySearchCondition(any(), any());

    }

    @Test
    @DisplayName("지역정보 코드로 조회 API 테스트")
    void findByCode() throws Exception {

        given(locationService.findByCode(any()))
                .willReturn(TEST_LOCATION_RESPONSE1);

        mockMvc.perform(get("/locations/code")
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .param("code", TEST_LOCATION_SEARCH_REQUEST.getSearchName()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_LOCATION_RESPONSE1)))
                .andDo(document("location/findByCode",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        requestParameters(
                                parameterWithName("code").description("법정동/행정동 코드")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("지역정보 ID"),
                                fieldWithPath("code").type(JsonFieldType.STRING).description("행정동/법정동 코드"),
                                fieldWithPath("city").type(JsonFieldType.STRING).description("시/도"),
                                fieldWithPath("gu").type(JsonFieldType.STRING).description("시/군/구"),
                                fieldWithPath("dong").type(JsonFieldType.STRING).description("읍/면/동"),
                                fieldWithPath("ri").type(JsonFieldType.STRING).description("리"),
                                fieldWithPath("let").type(JsonFieldType.NUMBER).description("위도"),
                                fieldWithPath("len").type(JsonFieldType.NUMBER).description("경도"),
                                fieldWithPath("codeType").type(JsonFieldType.STRING).description("코드타입")
                        )));

        then(locationService).should(times(1)).findByCode(any());
    }

    @Test
    @DisplayName("지역정보 ID로 주변 지역 정보 검색 API 테스트")
    void findAroundById() throws Exception {
        List<LocationResponse> result = Arrays.asList(TEST_LOCATION_RESPONSE1, TEST_LOCATION_RESPONSE2);

        given(locationService.findAroundById(any(), any()))
                .willReturn(result);

        mockMvc.perform(get("/locations/{locationId}/around", 1)
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .param("searchDistance","3"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)))
                .andDo(document("location/findAroundById",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        pathParameters(
                                parameterWithName("locationId").description("지역정보 ID")
                        ),
                        requestParameters(
                                parameterWithName("searchDistance").description("검색 거리")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("지역정보 ID"),
                                fieldWithPath("[].code").type(JsonFieldType.STRING).description("행정동/법정동 코드"),
                                fieldWithPath("[].city").type(JsonFieldType.STRING).description("시/도"),
                                fieldWithPath("[].gu").type(JsonFieldType.STRING).description("시/군/구"),
                                fieldWithPath("[].dong").type(JsonFieldType.STRING).description("읍/면/동"),
                                fieldWithPath("[].ri").type(JsonFieldType.STRING).description("리"),
                                fieldWithPath("[].let").type(JsonFieldType.NUMBER).description("위도"),
                                fieldWithPath("[].len").type(JsonFieldType.NUMBER).description("경도"),
                                fieldWithPath("[].codeType").type(JsonFieldType.STRING).description("코드타입")
                        )
                ));

        then(locationService).should(times(1)).findAroundById(any(),any());
    }
}