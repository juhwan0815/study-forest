package com.study.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.client.UserResponse;
import com.study.dto.GatheringResponse;
import com.study.service.GatheringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.Arrays;
import java.util.List;

import static com.study.GatheringFixture.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(GatheringController.class)
class GatheringControllerTest {

    private final String TEST_AUTHORIZATION = "Bearer *****";

    @MockBean
    private GatheringService gatheringService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(WebApplicationContext wac,
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
    @DisplayName("모임 생성 API")
    void create() throws Exception {
        // given
        given(gatheringService.create(any(), any(), any()))
                .willReturn(TEST_GATHERING_RESPONSE);

        // when
        mockMvc.perform(post("/studies/{studyId}/gatherings", 1)
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(TEST_GATHERING_CREATE_REQUEST)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_GATHERING_RESPONSE)))
                .andDo(document("gathering/create",
                        requestHeaders(
                                headerWithName("Authorization").description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("studyId").description("스터디 ID")
                        ),
                        requestFields(
                                fieldWithPath("gatheringTime").type(JsonFieldType.STRING).description("모임 시간"),
                                fieldWithPath("offline").type(JsonFieldType.BOOLEAN).description("모임 오프라인 여부"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("모임 설명"),
                                fieldWithPath("placeName").type(JsonFieldType.STRING).description("모임 장소"),
                                fieldWithPath("let").type(JsonFieldType.NUMBER).description("좌표 위도"),
                                fieldWithPath("len").type(JsonFieldType.NUMBER).description("좌표 경도")
                        ),
                        responseFields(
                                fieldWithPath("gatheringId").type(JsonFieldType.NUMBER).description("모임 ID"),
                                fieldWithPath("studyId").type(JsonFieldType.NUMBER).description("모임이 속한 스터디 ID"),
                                fieldWithPath("gatheringTime").type(JsonFieldType.STRING).description("모임 시간"),
                                fieldWithPath("numberOfPeople").type(JsonFieldType.NUMBER).description("모임 인원 수"),
                                fieldWithPath("offline").type(JsonFieldType.BOOLEAN).description("모임 오프라인 여부"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("모임 설명"),
                                fieldWithPath("place").type(JsonFieldType.OBJECT).description("모임 장소"),
                                fieldWithPath("place.name").type(JsonFieldType.STRING).description("모임 장소 이름"),
                                fieldWithPath("place.let").type(JsonFieldType.NUMBER).description("모임 장소 위도"),
                                fieldWithPath("place.len").type(JsonFieldType.NUMBER).description("모임 장소 경도")
                        )));

        // then
        then(gatheringService).should(times(1)).create(any(), any(), any());
    }

    @Test
    @DisplayName("스터디 모임 조회 API")
    void findByStudyId() throws Exception {

        Slice<GatheringResponse> result = new SliceImpl<>(Arrays.asList(TEST_GATHERING_RESPONSE), PageRequest.of(0, 10), true);

        // given
        given(gatheringService.findByStudyId(any(), any()))
                .willReturn(result);

        // when
        mockMvc.perform(get("/studies/{studyId}/gatherings", 1)
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)))
                .andDo(document("gathering/findByStudyId",
                        requestHeaders(
                                headerWithName("Authorization").description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("studyId").description("스터디 ID")
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("페이지 사이즈")
                        ),
                        responseFields(
                                fieldWithPath("content").type(JsonFieldType.ARRAY).description("조회 결과 배열"),
                                fieldWithPath("content.[].gatheringId").type(JsonFieldType.NUMBER).description("모임 ID"),
                                fieldWithPath("content.[].studyId").type(JsonFieldType.NUMBER).description("스터디 ID"),
                                fieldWithPath("content.[].gatheringTime").type(JsonFieldType.STRING).description("모임 날짜 및 시간"),
                                fieldWithPath("content.[].numberOfPeople").type(JsonFieldType.NUMBER).description("모임 현재 참가 인원 수"),
                                fieldWithPath("content.[].offline").type(JsonFieldType.BOOLEAN).description("모임 형태 ONLINE/OFFLINE"),
                                fieldWithPath("content.[].content").type(JsonFieldType.STRING).description("모임 설명"),
                                fieldWithPath("content.[].place").type(JsonFieldType.OBJECT).optional().description("모임 장소"),
                                fieldWithPath("content.[].place.name").type(JsonFieldType.STRING).description("모임 장소명"),
                                fieldWithPath("content.[].place.let").type(JsonFieldType.NUMBER).description("모임 장소 위도"),
                                fieldWithPath("content.[].place.len").type(JsonFieldType.NUMBER).description("모임 장소 경도"),
                                fieldWithPath("pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부 "),
                                fieldWithPath("pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"),
                                fieldWithPath("pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("값이 비었는지 여부"),
                                fieldWithPath("pageable.offset").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                fieldWithPath("pageable.pageNumber").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                fieldWithPath("pageable.pageSize").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                fieldWithPath("pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 여부"),
                                fieldWithPath("pageable.unpaged").type(JsonFieldType.BOOLEAN).description("비페이징 여부"),
                                fieldWithPath("last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                                fieldWithPath("size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                fieldWithPath("number").type(JsonFieldType.NUMBER).description("페이지 번호"),
                                fieldWithPath("sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬여부"),
                                fieldWithPath("sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬여부"),
                                fieldWithPath("sort.empty").type(JsonFieldType.BOOLEAN).description("값이 비었는지 여부"),
                                fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지 크기"),
                                fieldWithPath("first").type(JsonFieldType.BOOLEAN).description("처음 페이지 여부"),
                                fieldWithPath("empty").type(JsonFieldType.BOOLEAN).description("값이 비었는지 여부")
                        )));

        // then
        then(gatheringService).should(times(1)).findByStudyId(any(), any());
    }

    @Test
    @DisplayName("모임 수정 API")
    void update() throws Exception {
        // given
        given(gatheringService.update(any(), any(), any()))
                .willReturn(TEST_GATHERING_RESPONSE);

        // when
        mockMvc.perform(patch("/gatherings/{gatheringId}", 1)
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(TEST_GATHERING_UPDATE_REQUEST)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_GATHERING_RESPONSE)))
                .andDo(document("gathering/update",
                        requestHeaders(
                                headerWithName("Authorization").description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("gatheringId").description("모임 ID")
                        ),
                        requestFields(
                                fieldWithPath("gatheringTime").type(JsonFieldType.STRING).description("모임 시간"),
                                fieldWithPath("offline").type(JsonFieldType.BOOLEAN).description("모임 오프라인 여부"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("모임 설명"),
                                fieldWithPath("placeName").type(JsonFieldType.STRING).description("모임 장소"),
                                fieldWithPath("let").type(JsonFieldType.NUMBER).description("좌표 위도"),
                                fieldWithPath("len").type(JsonFieldType.NUMBER).description("좌표 경도")
                        ),
                        responseFields(
                                fieldWithPath("gatheringId").type(JsonFieldType.NUMBER).description("모임 ID"),
                                fieldWithPath("studyId").type(JsonFieldType.NUMBER).description("모임이 속한 스터디 ID"),
                                fieldWithPath("gatheringTime").type(JsonFieldType.STRING).description("모임 시간"),
                                fieldWithPath("numberOfPeople").type(JsonFieldType.NUMBER).description("모임 인원 수"),
                                fieldWithPath("offline").type(JsonFieldType.BOOLEAN).description("모임 오프라인 여부"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("모임 설명"),
                                fieldWithPath("place").type(JsonFieldType.OBJECT).description("모임 장소"),
                                fieldWithPath("place.name").type(JsonFieldType.STRING).description("모임 장소 이름"),
                                fieldWithPath("place.let").type(JsonFieldType.NUMBER).description("모임 장소 위도"),
                                fieldWithPath("place.len").type(JsonFieldType.NUMBER).description("모임 장소 경도")
                        )));

        // then
        then(gatheringService).should(times(1)).update(any(), any(), any());
    }

    @Test
    @DisplayName("모임 삭제 API")
    void delete() throws Exception {
        // given
        willDoNothing()
                .given(gatheringService)
                .delete(any(), any());

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/gatherings/{gatheringId}", 1)
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION))
                .andExpect(status().isOk())
                .andDo(document("gathering/delete",
                        requestHeaders(
                                headerWithName("Authorization").description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("gatheringId").description("모임 ID")
                        )));

        // then
        then(gatheringService).should(times(1)).delete(any(), any());
    }

    @Test
    @DisplayName("모임 조회 API")
    void findById() throws Exception {
        given(gatheringService.findById(any()))
                .willReturn(TEST_GATHERING_RESPONSE);

        // when
        mockMvc.perform(get("/gatherings/{gatheringId}", 1)
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_GATHERING_RESPONSE)))
                .andDo(document("gathering/findById",
                        requestHeaders(
                                headerWithName("Authorization").description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("gatheringId").description("모임 ID")
                        ),
                        responseFields(
                                fieldWithPath("gatheringId").type(JsonFieldType.NUMBER).description("모임 ID"),
                                fieldWithPath("studyId").type(JsonFieldType.NUMBER).description("모임이 속한 스터디 ID"),
                                fieldWithPath("gatheringTime").type(JsonFieldType.STRING).description("모임 시간"),
                                fieldWithPath("numberOfPeople").type(JsonFieldType.NUMBER).description("모임 인원 수"),
                                fieldWithPath("offline").type(JsonFieldType.BOOLEAN).description("모임 오프라인 여부"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("모임 설명"),
                                fieldWithPath("place").type(JsonFieldType.OBJECT).description("모임 장소"),
                                fieldWithPath("place.name").type(JsonFieldType.STRING).description("모임 장소 이름"),
                                fieldWithPath("place.let").type(JsonFieldType.NUMBER).description("모임 장소 위도"),
                                fieldWithPath("place.len").type(JsonFieldType.NUMBER).description("모임 장소 경도")
                        )));

        // then
        then(gatheringService).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("모임 참가자 추가 API")
    void addGatheringUser() throws Exception {
        // given
        willDoNothing()
                .given(gatheringService)
                .addGatheringUser(any(), any());

        // when
        mockMvc.perform(post("/gatherings/{gatheringId}/gatheringUsers", 1)
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION))
                .andExpect(status().isOk())
                .andDo(document("gathering/gatheringUser/create",
                        requestHeaders(
                                headerWithName("Authorization").description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("gatheringId").description("모임 ID")
                        )));

        // then
        then(gatheringService).should(times(1)).addGatheringUser(any(), any());
    }

    @Test
    @DisplayName("모임 참가 취소 API")
    void deleteGatheringUser() throws Exception {
        // given
        willDoNothing()
                .given(gatheringService)
                .deleteGatheringUser(any(), any());

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/gatherings/{gatheringId}/gatheringUsers", 1)
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION))
                .andExpect(status().isOk())
                .andDo(document("gathering/gatheringUser/delete",
                        requestHeaders(
                                headerWithName("Authorization").description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("gatheringId").description("모임 ID")
                        )));

        // then
        then(gatheringService).should(times(1)).deleteGatheringUser(any(), any());
    }

    @Test
    @DisplayName("모임 참가자 조회 API")
    void findGatheringUsersById() throws Exception {
        // given
        List<UserResponse> result = Arrays.asList(TEST_USER_RESPONSE);
        given(gatheringService.findGatheringUserById(any()))
                .willReturn(result);

        // when
        mockMvc.perform(get("/gatherings/{gatheringId}/gatheringUsers", 1)
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)))
                .andDo(document("gathering/gatheringUser/find",
                        requestHeaders(
                                headerWithName("Authorization").description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("gatheringId").description("모임 ID")
                        ),
                        responseFields(
                                fieldWithPath("[].userId").type(JsonFieldType.NUMBER).description("회원 ID"),
                                fieldWithPath("[].role").type(JsonFieldType.STRING).description("회원 권한"),
                                fieldWithPath("[].nickName").type(JsonFieldType.STRING).description("회원 닉네임"),
                                fieldWithPath("[].ageRange").type(JsonFieldType.STRING).description("회원 나이대"),
                                fieldWithPath("[].gender").type(JsonFieldType.STRING).description("회원 성별"),
                                fieldWithPath("[].imageUrl").type(JsonFieldType.STRING).description("회원 이미지 URL"),
                                fieldWithPath("[].areaId").type(JsonFieldType.NUMBER).description("회원 지역 ID"),
                                fieldWithPath("[].distance").type(JsonFieldType.NUMBER).description("회원 검색 거리"),
                                fieldWithPath("[].fcmToken").type(JsonFieldType.STRING).description("회원 FCM 토큰")
                        )
                ));

        // then
        then(gatheringService).should(times(1)).findGatheringUserById(any());
    }
}