package com.study.gatheringservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.gatheringservice.GatheringFixture;
import com.study.gatheringservice.config.LoginUserArgumentResolver;
import com.study.gatheringservice.domain.Gathering;
import com.study.gatheringservice.model.gathering.GatheringResponse;
import com.study.gatheringservice.model.gatheringuser.GatheringUserResponse;
import com.study.gatheringservice.service.GatheringService;
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.study.gatheringservice.GatheringFixture.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
        given(gatheringService.create(any(), any(), any()))
                .willReturn(TEST_GATHERING_RESPONSE2);

        // when
        mockMvc.perform(post("/studies/{studyId}/gatherings", 1)
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
                                fieldWithPath("place.len").type(JsonFieldType.NUMBER).description("모임 장소 경도")
                        )));

        // then
        then(gatheringService).should(times(1)).create(any(), any(), any());
    }

    @Test
    @DisplayName("모임 수정 API 테스트")
    void update() throws Exception {
        // given
        given(gatheringService.update(any(), any(), any()))
                .willReturn(TEST_GATHERING_RESPONSE3);

        // when
        mockMvc.perform(patch("/gatherings/{gatheringId}", 1)
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(TEST_GATHERING_UPDATE_REQUEST2)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_GATHERING_RESPONSE3)))
                .andDo(document("gathering/update",
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("gatheringId").description("모임 ID")
                        ),
                        requestFields(
                                fieldWithPath("gatheringTime").type(JsonFieldType.STRING).description("변경할 모임 시간"),
                                fieldWithPath("shape").type(JsonFieldType.STRING).description("변경할 모임 형태 ONLINE / OFFLINE"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("변경할 모임 설명"),
                                fieldWithPath("placeName").type(JsonFieldType.STRING).description("변경할 모임 장소"),
                                fieldWithPath("let").type(JsonFieldType.NUMBER).description("변경할 좌표 위도"),
                                fieldWithPath("len").type(JsonFieldType.NUMBER).description("변경할 좌표 경도")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("모임 ID"),
                                fieldWithPath("studyId").type(JsonFieldType.NUMBER).description("모임이 속한 스터디 ID"),
                                fieldWithPath("gatheringTime").type(JsonFieldType.STRING).description("변경된 모임 시간"),
                                fieldWithPath("numberOfPeople").type(JsonFieldType.NUMBER).description("모임 인원 수"),
                                fieldWithPath("shape").type(JsonFieldType.STRING).description("변경된 모임 형태 ONLINE/OFFLINE"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("변경된 모임 설명"),
                                fieldWithPath("place").type(JsonFieldType.OBJECT).description("변경된 모임 장소"),
                                fieldWithPath("place.name").type(JsonFieldType.STRING).description("변경된 모임 장소 이름"),
                                fieldWithPath("place.let").type(JsonFieldType.NUMBER).description("변경된 모임 장소 위도"),
                                fieldWithPath("place.len").type(JsonFieldType.NUMBER).description("변경된 모임 장소 경도")
                        )));

        // then
        then(gatheringService).should(times(1)).update(any(), any(), any());
    }

    @Test
    @DisplayName("모임 삭제 API 테스트")
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
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("gatheringId").description("모임 ID")
                        )));

        // then
        then(gatheringService).should(times(1)).delete(any(), any());
    }

    @Test
    @DisplayName("모임 상세 조회 API 테스트")
    void findById() throws Exception {

        // given
        given(gatheringService.findById(any(), any()))
                .willReturn(TEST_GATHERING_RESPONSE4);

        // when
        mockMvc.perform(get("/gatherings/{gatheringId}", 1)
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_GATHERING_RESPONSE4)))
                .andDo(document("gathering/findById",
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("gatheringId").description("모임 ID")
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
                                fieldWithPath("place.len").type(JsonFieldType.NUMBER).description("모임 장소소 경도"),
                                fieldWithPath("apply").type(JsonFieldType.BOOLEAN).description("모임 신청 여부 null일 경우 모임 등록자")
                        )));

        // then
        then(gatheringService).should(times(1)).findById(any(), any());
    }

    @Test
    @DisplayName("모임 조회 API 테스트")
    void find() throws Exception {
        // given
        List<GatheringResponse> gatherings = new ArrayList<>();
        gatherings.add(TEST_GATHERING_RESPONSE1);
        gatherings.add(TEST_GATHERING_RESPONSE2);

        PageRequest pageable = PageRequest.of(0, 10);
        Page<GatheringResponse> pageGathering = new PageImpl<>(gatherings, pageable, gatherings.size());

        // given
        given(gatheringService.find(any(), any()))
                .willReturn(pageGathering);

        // when
        mockMvc.perform(get("/studies/{studyId}/gatherings", 1)
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .param("page","0")
                .param("size","20"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(pageGathering)))
                .andDo(document("gathering/find",
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
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
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("모임 ID"),
                                fieldWithPath("content.[].studyId").type(JsonFieldType.NUMBER).description("스터디 ID"),
                                fieldWithPath("content.[].gatheringTime").type(JsonFieldType.STRING).description("모임 날짜 및 시간"),
                                fieldWithPath("content.[].numberOfPeople").type(JsonFieldType.NUMBER).description("모임 현재 참가 인원 수"),
                                fieldWithPath("content.[].shape").type(JsonFieldType.STRING).description("모임 형태 ONLINE/OFFLINE"),
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
                        )));

        // then
        then(gatheringService).should(times(1)).find(any(), any());
    }

    @Test
    @DisplayName("모임 참가 API 테스트")
    void addGatheringUser() throws Exception {
        // given
        willDoNothing()
                .given(gatheringService)
                .addGatheringUser(any(), any());

        // when
        mockMvc.perform(post("/gatherings/{gatheringId}/users", 1)
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION))
                .andExpect(status().isOk())
                .andDo(document("gathering/gatheringUser/create",
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("gatheringId").description("모임 ID")
                        )));

        // then
        then(gatheringService).should(times(1)).addGatheringUser(any(), any());
    }

    @Test
    @DisplayName("모임 참가 취소 API 테스트")
    void deleteGatheringUser() throws Exception {
        // given
        willDoNothing()
                .given(gatheringService)
                .deleteGatheringUser(any(), any());

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/gatherings/{gatheringId}/users", 1)
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION))
                .andExpect(status().isOk())
                .andDo(document("gathering/gatheringUser/delete",
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("gatheringId").description("모임 ID")
                        )));

        // then
        then(gatheringService).should(times(1)).deleteGatheringUser(any(), any());
    }

    @Test
    @DisplayName("모임 참가자 조회 API 테스트")
    void findGatheringUsers() throws Exception {
        // given
        List<GatheringUserResponse> gatheringUsers = new ArrayList<>();
        gatheringUsers.add(TEST_GATHERING_USER_RESPONSE1);
        gatheringUsers.add(TEST_GATHERING_USER_RESPONSE2);

        given(gatheringService.findGatheringUsers(any()))
                .willReturn(gatheringUsers);
        // when
        mockMvc.perform(get("/gatherings/{gatheringId}/users", 1)
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(gatheringUsers)))
                .andDo(document("gathering/gatheringUser/find",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        pathParameters(
                                parameterWithName("gatheringId").description("모임 ID")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("모임 참가 ID"),
                                fieldWithPath("[].userId").type(JsonFieldType.NUMBER).description("모임 참가 유저 ID"),
                                fieldWithPath("[].nickName").type(JsonFieldType.STRING).description("모임 참가 유저 닉네임"),
                                fieldWithPath("[].image").type(JsonFieldType.OBJECT).description("모임 참가 유저 이미지"),
                                fieldWithPath("[].image.thumbnailImage").type(JsonFieldType.STRING).description("모임 참가 유저 썸네일 이미지 URL"),
                                fieldWithPath("[].image.profileImage").type(JsonFieldType.STRING).description("모임 참가  유저 이미지 URL"),
                                fieldWithPath("[].gender").type(JsonFieldType.STRING).description("모임 참가 유저 성별"),
                                fieldWithPath("[].ageRange").type(JsonFieldType.STRING).description("모임 참가 유저 나이대"),
                                fieldWithPath("[].register").type(JsonFieldType.BOOLEAN).description("모임 등록자 여부")
                        )
                ));

        // then
        then(gatheringService).should(times(1)).findGatheringUsers(any());
    }

}