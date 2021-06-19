package com.study.studyservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.studyservice.config.LoginUserArgumentResolver;
import com.study.studyservice.domain.StudyStatus;
import com.study.studyservice.fixture.StudyFixture;
import com.study.studyservice.model.category.response.CategoryResponse;
import com.study.studyservice.model.location.response.LocationResponse;
import com.study.studyservice.model.study.request.StudyCreateRequest;
import com.study.studyservice.model.study.request.StudyUpdateRequest;
import com.study.studyservice.model.study.response.StudyResponse;
import com.study.studyservice.model.waituser.WaitUserResponse;
import com.study.studyservice.service.StudyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.study.studyservice.fixture.StudyFixture.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(StudyController.class)
class StudyControllerTest {

    private final String TEST_AUTHORIZATION = "Bearer 액세스토큰";

    @MockBean
    private StudyService studyService;

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
    @DisplayName("스터디 생성 API 테스트")
    void create() throws Exception {

        MockMultipartFile request = new MockMultipartFile("request",
                "request",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(TEST_STUDY_CREATE_REQUEST1).getBytes(StandardCharsets.UTF_8));

        given(studyService.create(any(), any(), any()))
                .willReturn(TEST_STUDY_RESPONSE1);

        given(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);

        mockMvc.perform(multipart("/studies").file(TEST_IMAGE_FILE).file(request)
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_STUDY_RESPONSE1)))
                .andDo(document("study/create",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        requestParts(
                                partWithName("image").description("스터디 이미지 파일").optional(),
                                partWithName("request").description("스터디 생성 요청 데이터")
                        ),
                        requestPartFields("request",
                                fieldWithPath("name").type(JsonFieldType.STRING).description("스터디 이름"),
                                fieldWithPath("numberOfPeople").type(JsonFieldType.NUMBER).description("스터디 인원"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("스터디 내용"),
                                fieldWithPath("tags").type(JsonFieldType.ARRAY).description("스터디 주제(태그)"),
                                fieldWithPath("online").type(JsonFieldType.BOOLEAN).description("스터디 온라인 여부"),
                                fieldWithPath("offline").type(JsonFieldType.BOOLEAN).description("스터디 오프라인여부"),
                                fieldWithPath("locationCode").type(JsonFieldType.STRING).description("지역 코드"),
                                fieldWithPath("categoryId").type(JsonFieldType.NUMBER).description("카테고리 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("스터디 ID"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("스터디 이름"),
                                fieldWithPath("numberOfPeople").type(JsonFieldType.NUMBER).description("스터디 인원"),
                                fieldWithPath("currentNumberOfPeople").type(JsonFieldType.NUMBER).description("스터디 현재 인원"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("스터디 내용"),
                                fieldWithPath("online").type(JsonFieldType.BOOLEAN).description("스터디 온라인 여부"),
                                fieldWithPath("offline").type(JsonFieldType.BOOLEAN).description("스터디 오프라인여부"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("스터디 상태"),
                                fieldWithPath("image").type(JsonFieldType.OBJECT).description("스터디 이미지"),
                                fieldWithPath("image.studyImage").type(JsonFieldType.STRING).description("스터디 이미지 URL"),
                                fieldWithPath("image.thumbnailImage").type(JsonFieldType.STRING).description("스터디 썸네일 이미지 URL"),
                                fieldWithPath("location").type(JsonFieldType.OBJECT).description("지역 정보"),
                                fieldWithPath("location.id").type(JsonFieldType.NUMBER).description("지역 ID"),
                                fieldWithPath("location.code").type(JsonFieldType.STRING).description("지역 코드"),
                                fieldWithPath("location.city").type(JsonFieldType.STRING).description("지역 시/도"),
                                fieldWithPath("location.gu").type(JsonFieldType.STRING).description("지역 시/군/구"),
                                fieldWithPath("location.dong").type(JsonFieldType.STRING).description("지역 읍/면/동"),
                                fieldWithPath("location.ri").type(JsonFieldType.STRING).description("지역 --리"),
                                fieldWithPath("location.let").type(JsonFieldType.NUMBER).description("지역 위도"),
                                fieldWithPath("location.len").type(JsonFieldType.NUMBER).description("지역 경도"),
                                fieldWithPath("location.codeType").type(JsonFieldType.STRING).description("지역 코드 타입"),
                                fieldWithPath("parentCategory").type(JsonFieldType.OBJECT).description("부모 카테고리"),
                                fieldWithPath("parentCategory.id").type(JsonFieldType.NUMBER).description("부모 카테고리 ID"),
                                fieldWithPath("parentCategory.name").type(JsonFieldType.STRING).description("부모 카테고리 이름"),
                                fieldWithPath("childCategory").type(JsonFieldType.OBJECT).description("자식 카테고리"),
                                fieldWithPath("childCategory.id").type(JsonFieldType.NUMBER).description("자식 카테고리 ID"),
                                fieldWithPath("childCategory.name").type(JsonFieldType.STRING).description("자식 카테고리 이름"),
                                fieldWithPath("studyTags").type(JsonFieldType.ARRAY).description("스터디 태그")
                        )
                ));

        then(studyService).should(times(1)).create(any(), any(), any());
    }

    @Test
    @DisplayName("스터디 수정 API 테스트")
    void update() throws Exception {
        MockMultipartFile request = new MockMultipartFile("request",
                "request",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(TEST_STUDY_UPDATE_REQUEST4)
                        .getBytes(StandardCharsets.UTF_8));

        MockMultipartHttpServletRequestBuilder builder =
                RestDocumentationRequestBuilders.fileUpload("/studies/{studyId}", 1);
        builder.with(request1 -> {
            request1.setMethod(HttpMethod.PATCH.name());
            return request1;
        });

        given(studyService.update(any(), any(), any(), any()))
                .willReturn(TEST_STUDY_RESPONSE2);

        given(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);

        mockMvc.perform(builder.file(TEST_IMAGE_FILE).file(request)
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_STUDY_RESPONSE2)))
                .andDo(document("study/update",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        requestParts(
                                partWithName("image").description("변경할 이미지 파일").optional(),
                                partWithName("request").description("스터디 수정 요청 데이터")
                        ),
                        requestPartFields("request",
                                fieldWithPath("name").type(JsonFieldType.STRING).description("변경할 스터디 이름"),
                                fieldWithPath("numberOfPeople").type(JsonFieldType.NUMBER).description("변경할 스터디 인원 수"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("변경할 스터디 내용"),
                                fieldWithPath("categoryId").type(JsonFieldType.NUMBER).description("변경할 스터디 카테고리 ID"),
                                fieldWithPath("tags").type(JsonFieldType.ARRAY).description("변경할 스터디 태그"),
                                fieldWithPath("deleteImage").type(JsonFieldType.BOOLEAN).description("스터디 이미지 삭제 여부"),
                                fieldWithPath("close").type(JsonFieldType.BOOLEAN).description("스터디 비공개 여부"),
                                fieldWithPath("online").type(JsonFieldType.BOOLEAN).description("스터디 온라인 여부"),
                                fieldWithPath("offline").type(JsonFieldType.BOOLEAN).description("스터디 오프라인 여부"),
                                fieldWithPath("locationCode").type(JsonFieldType.STRING).description("스터디 지역 코드")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("스터디 ID"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("변경된 스터디 이름"),
                                fieldWithPath("numberOfPeople").type(JsonFieldType.NUMBER).description("변경된 스터디 인원"),
                                fieldWithPath("currentNumberOfPeople").type(JsonFieldType.NUMBER).description("스터디 현재 인원"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("변경된 스터디 내용"),
                                fieldWithPath("online").type(JsonFieldType.BOOLEAN).description("스터디 온라인 여부"),
                                fieldWithPath("offline").type(JsonFieldType.BOOLEAN).description("스터디 오프라인여부"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("스터디 상태"),
                                fieldWithPath("image").type(JsonFieldType.OBJECT).description("변경된 스터디 이미지"),
                                fieldWithPath("image.studyImage").type(JsonFieldType.STRING).description("변경된 스터디 이미지 URL"),
                                fieldWithPath("image.thumbnailImage").type(JsonFieldType.STRING).description("변경된 스터디 썸네일 이미지 URL"),
                                fieldWithPath("location").type(JsonFieldType.OBJECT).description("지역 정보"),
                                fieldWithPath("location.id").type(JsonFieldType.NUMBER).description("지역 ID"),
                                fieldWithPath("location.code").type(JsonFieldType.STRING).description("지역 코드"),
                                fieldWithPath("location.city").type(JsonFieldType.STRING).description("지역 시/도"),
                                fieldWithPath("location.gu").type(JsonFieldType.STRING).description("지역 시/군/구"),
                                fieldWithPath("location.dong").type(JsonFieldType.STRING).description("지역 읍/면/동"),
                                fieldWithPath("location.ri").type(JsonFieldType.STRING).description("지역 --리"),
                                fieldWithPath("location.let").type(JsonFieldType.NUMBER).description("지역 위도"),
                                fieldWithPath("location.len").type(JsonFieldType.NUMBER).description("지역 경도"),
                                fieldWithPath("location.codeType").type(JsonFieldType.STRING).description("지역 코드 타입"),
                                fieldWithPath("parentCategory").type(JsonFieldType.OBJECT).description("부모 카테고리"),
                                fieldWithPath("parentCategory.id").type(JsonFieldType.NUMBER).description("부모 카테고리 ID"),
                                fieldWithPath("parentCategory.name").type(JsonFieldType.STRING).description("부모 카테고리 이름"),
                                fieldWithPath("childCategory").type(JsonFieldType.OBJECT).description("자식 카테고리"),
                                fieldWithPath("childCategory.id").type(JsonFieldType.NUMBER).description("자식 카테고리 ID"),
                                fieldWithPath("childCategory.name").type(JsonFieldType.STRING).description("자식 카테고리 이름"),
                                fieldWithPath("studyTags").type(JsonFieldType.ARRAY).description("스터디 태그")
                        )
                ));

        then(studyService).should(times(1)).update(any(), any(), any(), any());
    }

    @Test
    @DisplayName("스터디 삭제 API 테스트")
    void delete() throws Exception {
        // given
        willDoNothing()
                .given(studyService)
                .delete(any(), any());

        given(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/studies/{studyId}", 1)
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION))
                .andExpect(status().isOk())
                .andDo(document("study/delete",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        pathParameters(
                                parameterWithName("studyId").description("삭제할 스터디 ID")
                        )
                ));

        // then
        then(studyService).should(times(1)).delete(any(), any());
    }

    @Test
    @DisplayName("스터디 상세 조회 API 테스트")
    void findById() throws Exception {
        // given
        given(studyService.findById(any()))
                .willReturn(TEST_STUDY_RESPONSE1);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/studies/{studyId}", 1)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_STUDY_RESPONSE1)))
                .andDo(document("study/findById",
                        pathParameters(
                                parameterWithName("studyId").description("조회할 스터디 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("스터디 ID"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("스터디 이름"),
                                fieldWithPath("numberOfPeople").type(JsonFieldType.NUMBER).description("스터디 인원"),
                                fieldWithPath("currentNumberOfPeople").type(JsonFieldType.NUMBER).description("스터디 현재 인원"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("스터디 내용"),
                                fieldWithPath("online").type(JsonFieldType.BOOLEAN).description("스터디 온라인 여부"),
                                fieldWithPath("offline").type(JsonFieldType.BOOLEAN).description("스터디 오프라인여부"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("스터디 상태"),
                                fieldWithPath("image").type(JsonFieldType.OBJECT).description("스터디 이미지"),
                                fieldWithPath("image.studyImage").type(JsonFieldType.STRING).description("스터디 이미지 URL"),
                                fieldWithPath("image.thumbnailImage").type(JsonFieldType.STRING).description("스터디 썸네일 이미지 URL"),
                                fieldWithPath("location").type(JsonFieldType.OBJECT).description("지역 정보"),
                                fieldWithPath("location.id").type(JsonFieldType.NUMBER).description("지역 ID"),
                                fieldWithPath("location.code").type(JsonFieldType.STRING).description("지역 코드"),
                                fieldWithPath("location.city").type(JsonFieldType.STRING).description("지역 시/도"),
                                fieldWithPath("location.gu").type(JsonFieldType.STRING).description("지역 시/군/구"),
                                fieldWithPath("location.dong").type(JsonFieldType.STRING).description("지역 읍/면/동"),
                                fieldWithPath("location.ri").type(JsonFieldType.STRING).description("지역 --리"),
                                fieldWithPath("location.let").type(JsonFieldType.NUMBER).description("지역 위도"),
                                fieldWithPath("location.len").type(JsonFieldType.NUMBER).description("지역 경도"),
                                fieldWithPath("location.codeType").type(JsonFieldType.STRING).description("지역 코드 타입"),
                                fieldWithPath("parentCategory").type(JsonFieldType.OBJECT).description("부모 카테고리"),
                                fieldWithPath("parentCategory.id").type(JsonFieldType.NUMBER).description("부모 카테고리 ID"),
                                fieldWithPath("parentCategory.name").type(JsonFieldType.STRING).description("부모 카테고리 이름"),
                                fieldWithPath("childCategory").type(JsonFieldType.OBJECT).description("자식 카테고리"),
                                fieldWithPath("childCategory.id").type(JsonFieldType.NUMBER).description("자식 카테고리 ID"),
                                fieldWithPath("childCategory.name").type(JsonFieldType.STRING).description("자식 카테고리 이름"),
                                fieldWithPath("studyTags").type(JsonFieldType.ARRAY).description("스터디 태그")
                        )
                ));

        // then
        then(studyService).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("스터디 참가 신청 API 테스트")
    void addWaitUser() throws Exception {
        willDoNothing()
                .given(studyService)
                .createWaitUser(any(), any());

        given(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/studies/{studyId}/waitUsers", 1)
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION))
                .andExpect(status().isOk())
                .andDo(document("study/waitUser/create",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        pathParameters(
                                parameterWithName("studyId").description("참여할 스터디 ID")
                        )
                ));

        then(studyService).should(times(1)).createWaitUser(any(),any());
    }

    @Test
    @DisplayName("스터디 참가 대기 인원 조회 API 테스트")
    void findWaitUser() throws Exception{
        List<WaitUserResponse> waitUserResponseList = Arrays.asList(TEST_WAIT_USER_RESPONSE1, TEST_WAIT_USER_RESPONSE2);

        given(studyService.findWaitUsersByStudyId(any()))
                .willReturn(waitUserResponseList);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/studies/{studyId}/waitUsers", 1)
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(waitUserResponseList)))
                .andDo(document("study/waitUser/find",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        pathParameters(
                                parameterWithName("studyId").description("참여할 스터디 ID")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("스터디 참가 대기 ID"),
                                fieldWithPath("[].userId").type(JsonFieldType.NUMBER).description("스터디 참가 대기 유저 ID"),
                                fieldWithPath("[].nickName").type(JsonFieldType.STRING).description("스터디 참가 대기 유저 닉네임")
                        )
                ));
        then(studyService).should(times(1)).findWaitUsersByStudyId(any());
    }

    @Test
    @DisplayName("스터디 참가 인원 추가 API 테스트")
    void createStudyUser() throws Exception{
        willDoNothing()
                .given(studyService)
                .createStudyUser(any(),any(),any());

        given(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/studies/{studyId}/users/{userId}", 1,2)
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION))
                .andExpect(status().isOk())
                .andDo(document("study/studyUser/create",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        pathParameters(
                                parameterWithName("studyId").description("스터디 ID"),
                                parameterWithName("userId").description("스터디 참가 대기 목록의 회원 ID")
                        )
                ));

        then(studyService).should(times(1)).createStudyUser(any(),any(),any());
    }

}