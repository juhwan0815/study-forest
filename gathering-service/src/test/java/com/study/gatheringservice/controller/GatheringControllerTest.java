package com.study.gatheringservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.gatheringservice.GatheringFixture;
import com.study.gatheringservice.config.LoginUserArgumentResolver;
import com.study.gatheringservice.model.gathering.GatheringResponse;
import com.study.gatheringservice.service.GatheringService;
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

import java.util.ArrayList;
import java.util.List;

import static com.study.gatheringservice.GatheringFixture.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(GatheringController.class)
class GatheringControllerTest {

    private final String TEST_AUTHORIZATION = "Bearer 액세스토큰";

    @MockBean
    private GatheringService gatheringService;

    @MockBean
    private LoginUserArgumentResolver loginUserArgumentResolver;

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
    @DisplayName("모임 생성 API 테스트")
    void create() throws Exception {
        // given
        given(gatheringService.create(any(),any(),any()))
                .willReturn(TEST_GATHERING_RESPONSE2);

        // when
        mockMvc.perform(post("/studies/{studyId}/gatherings",1)
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(TEST_GATHERING_CREATE_REQUEST2)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_GATHERING_RESPONSE2)))
                .andDo(document("gathering/create",
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("studyId").description("스터디 ID")
                        ),
                        requestFields(
                                fieldWithPath("gatheringTime").type(JsonFieldType.STRING).description("모임 시간"),
                                fieldWithPath("shape").type(JsonFieldType.STRING).description("모임 형태 ONLINE / OFFLINE"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("모임 설명"),
                                fieldWithPath("placeName").type(JsonFieldType.STRING).description("모임 장소"),
                                fieldWithPath("let").type(JsonFieldType.NUMBER).description("좌표 위도"),
                                fieldWithPath("len").type(JsonFieldType.NUMBER).description("좌표 경도")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("모임 ID"),
                                fieldWithPath("studyId").type(JsonFieldType.NUMBER).description("모임이 속한 스터디 ID"),
                                fieldWithPath("gatheringTime").type(JsonFieldType.STRING).description("모임 시간"),
                                fieldWithPath("numberOfPeople").type(JsonFieldType.NUMBER).description("모임 인원 수"),
                                fieldWithPath("shape").type(JsonFieldType.STRING).description("모임 형태 ONLINE/OFFLINE"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("모임 설명"),
                                fieldWithPath("place").type(JsonFieldType.OBJECT).description("모임 장소"),
                                fieldWithPath("place.name").type(JsonFieldType.STRING).description("모임 장소 이름"),
                                fieldWithPath("place.let").type(JsonFieldType.NUMBER).description("모임 장소 위도"),
                                fieldWithPath("place.len").type(JsonFieldType.NUMBER).description("모임 종소 경도")
                        )));

        // then
        then(gatheringService).should(times(1)).create(any(),any(),any());
    }



}