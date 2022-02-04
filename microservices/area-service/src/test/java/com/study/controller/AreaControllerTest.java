package com.study.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.dto.AreaResponse;
import com.study.service.AreaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.Arrays;
import java.util.List;

import static com.study.AreaFixture.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(AreaController.class)
class AreaControllerTest {

    @MockBean
    private AreaService areaService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp(WebApplicationContext wac,
                      RestDocumentationContextProvider restDocumentationContextProvider) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .alwaysDo(print())
                .addFilters(new CharacterEncodingFilter("utf-8", true))
                .apply(documentationConfiguration(restDocumentationContextProvider)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();
    }

    @Test
    @DisplayName("지역 생성 API")
    void create() throws Exception {
        // given
        willDoNothing()
                .given(areaService)
                .create(any());

        // when
        mockMvc.perform(post("/areas")
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Arrays.asList(TEST_AREA_CREATE_REQUEST))))
                .andExpect(status().isOk())
                .andDo(document("area/create",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        requestFields(
                                fieldWithPath("[]").type(JsonFieldType.ARRAY).description("지역 정보 리스트"),
                                fieldWithPath("[].code").type(JsonFieldType.STRING).description("지역 코드"),
                                fieldWithPath("[].city").type(JsonFieldType.STRING).description("특별시, 광역시, 도"),
                                fieldWithPath("[].gu").type(JsonFieldType.STRING).description("시, 군, 구"),
                                fieldWithPath("[].dong").type(JsonFieldType.STRING).description("읍, 면, 동"),
                                fieldWithPath("[].ri").type(JsonFieldType.STRING).description("리"),
                                fieldWithPath("[].let").type(JsonFieldType.NUMBER).description("위도"),
                                fieldWithPath("[].len").type(JsonFieldType.NUMBER).description("경도"),
                                fieldWithPath("[].codeType").type(JsonFieldType.STRING).description("법정동/행정동")
                        )));

        // then
        then(areaService).should(times(1)).create(any());
    }

    @Test
    @DisplayName("지역 상세 조회 API")
    void findById() throws Exception {
        // given
        given(areaService.findById(anyLong()))
                .willReturn(TEST_AREA_RESPONSE);

        // when
        mockMvc.perform(get("/areas/{areaId}", TEST_AREA.getId())
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_AREA_RESPONSE)))
                .andDo(document("area/findById",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("areaId").description("지역 ID")
                        ),
                        responseFields(
                                fieldWithPath("areaId").type(JsonFieldType.NUMBER).description("지역 ID"),
                                fieldWithPath("code").type(JsonFieldType.STRING).description("지역 코드"),
                                fieldWithPath("city").type(JsonFieldType.STRING).description("시/도"),
                                fieldWithPath("gu").type(JsonFieldType.STRING).description("시/군/구"),
                                fieldWithPath("dong").type(JsonFieldType.STRING).description("읍/면/동"),
                                fieldWithPath("ri").type(JsonFieldType.STRING).description("리"),
                                fieldWithPath("let").type(JsonFieldType.NUMBER).description("위도"),
                                fieldWithPath("len").type(JsonFieldType.NUMBER).description("경도"),
                                fieldWithPath("codeType").type(JsonFieldType.STRING).description("법정동/행정동")
                        )));

        // then
        then(areaService).should(times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("지역 검색 API")
    void findBySearchRequest() throws Exception {
        // given
        List<AreaResponse> result = Arrays.asList(TEST_AREA_RESPONSE);

        given(areaService.findBySearchRequest(any()))
                .willReturn(result);

        // when
        mockMvc.perform(get("/areas")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                        .param("areaId", String.valueOf(TEST_AREA.getId()))
                        .param("size", String.valueOf(TEST_SIZE))
                        .param("searchWord", TEST_AREA_SEARCH_REQUEST.getSearchWord()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)))
                .andDo(document("area/findBySearchRequest",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        requestParameters(
                                parameterWithName("searchWord").description("검색어"),
                                parameterWithName("size").description("페이지 사이즈"),
                                parameterWithName("areaId").description("마지막 지역 ID")
                        ),
                        responseFields(
                                fieldWithPath("[]").type(JsonFieldType.ARRAY).description("조회 결과 리스트"),
                                fieldWithPath("[].areaId").type(JsonFieldType.NUMBER).description("지역 ID"),
                                fieldWithPath("[].code").type(JsonFieldType.STRING).description("지역 코드"),
                                fieldWithPath("[].city").type(JsonFieldType.STRING).description("시/도"),
                                fieldWithPath("[].gu").type(JsonFieldType.STRING).description("시/군/구"),
                                fieldWithPath("[].dong").type(JsonFieldType.STRING).description("읍/면/동"),
                                fieldWithPath("[].ri").type(JsonFieldType.STRING).description("리"),
                                fieldWithPath("[].let").type(JsonFieldType.NUMBER).description("위도"),
                                fieldWithPath("[].len").type(JsonFieldType.NUMBER).description("경도"),
                                fieldWithPath("[].codeType").type(JsonFieldType.STRING).description("법정동/행정동")
                        )));

        // then
        then(areaService).should(times(1)).findBySearchRequest(any());
    }

    @Test
    @DisplayName("지역 코드 조회 API")
    void findByCode() throws Exception {
        // given
        given(areaService.findByCode(any()))
                .willReturn(TEST_AREA_RESPONSE);

        // when
        mockMvc.perform(get("/areas/code")
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .param("code", TEST_AREA_CODE_REQUEST.getCode()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_AREA_RESPONSE)))
                .andDo(document("area/findByCode",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        requestParameters(
                                parameterWithName("code").description("지역 코드")
                        ),
                        responseFields(
                                fieldWithPath("areaId").type(JsonFieldType.NUMBER).description("지역 ID"),
                                fieldWithPath("code").type(JsonFieldType.STRING).description("지역 코드"),
                                fieldWithPath("city").type(JsonFieldType.STRING).description("시/도"),
                                fieldWithPath("gu").type(JsonFieldType.STRING).description("시/군/구"),
                                fieldWithPath("dong").type(JsonFieldType.STRING).description("읍/면/동"),
                                fieldWithPath("ri").type(JsonFieldType.STRING).description("리"),
                                fieldWithPath("let").type(JsonFieldType.NUMBER).description("위도"),
                                fieldWithPath("len").type(JsonFieldType.NUMBER).description("경도"),
                                fieldWithPath("codeType").type(JsonFieldType.STRING).description("법정동/행정동")
                        )));

        // then
        then(areaService).should(times(1)).findByCode(any());
    }

    @Test
    @DisplayName("주변 지역 정보 검색 API")
    void findAroundById() throws Exception {
        // given
        List<AreaResponse> result = Arrays.asList(TEST_AREA_RESPONSE);

        given(areaService.findAroundById(any(), any()))
                .willReturn(result);

        // when
        mockMvc.perform(get("/areas/{areaId}/around", TEST_AREA.getId())
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .param("distance", String.valueOf(TEST_AREA_AROUND_REQUEST.getDistance())))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)))
                .andDo(document("area/findAroundById",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("areaId").description("지역 ID")
                        ),
                        requestParameters(
                                parameterWithName("distance").description("검색 거리")
                        ),
                        responseFields(
                                fieldWithPath("[]").type(JsonFieldType.ARRAY).description("지역 정보 리스트"),
                                fieldWithPath("[].areaId").type(JsonFieldType.NUMBER).description("지역 ID"),
                                fieldWithPath("[].code").type(JsonFieldType.STRING).description("지역 코드"),
                                fieldWithPath("[].city").type(JsonFieldType.STRING).description("시/도"),
                                fieldWithPath("[].gu").type(JsonFieldType.STRING).description("시/군/구"),
                                fieldWithPath("[].dong").type(JsonFieldType.STRING).description("읍/면/동"),
                                fieldWithPath("[].ri").type(JsonFieldType.STRING).description("리"),
                                fieldWithPath("[].let").type(JsonFieldType.NUMBER).description("위도"),
                                fieldWithPath("[].len").type(JsonFieldType.NUMBER).description("경도"),
                                fieldWithPath("[].codeType").type(JsonFieldType.STRING).description("법정동/행정동")
                        )
                ));

        // then
        then(areaService).should(times(1)).findAroundById(any(), any());
    }
}