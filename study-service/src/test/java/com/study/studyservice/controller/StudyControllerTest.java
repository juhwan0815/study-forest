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
import com.study.studyservice.model.studyuser.StudyUserResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        given(studyService.findById(any(),any()))
                .willReturn(TEST_STUDY_RESPONSE7);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/studies/{studyId}", 1)
                .header(HttpHeaders.AUTHORIZATION,TEST_AUTHORIZATION)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_STUDY_RESPONSE7)))
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
                                fieldWithPath("studyTags").type(JsonFieldType.ARRAY).description("스터디 태그"),
                                fieldWithPath("apply").type(JsonFieldType.BOOLEAN).description("스터디 참가 신청 여부 null 일 경우 이미 스터디 참가 회원")
                        )
                ));

        // then
        then(studyService).should(times(1)).findById(any(),any());
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

        then(studyService).should(times(1)).createWaitUser(any(), any());
    }

    @Test
    @DisplayName("스터디 참가 대기 인원 조회 API 테스트")
    void findWaitUser() throws Exception {
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
                                parameterWithName("studyId").description("스터디 ID")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("스터디 참가 대기 ID"),
                                fieldWithPath("[].userId").type(JsonFieldType.NUMBER).description("스터디 참가 대기 유저 ID"),
                                fieldWithPath("[].nickName").type(JsonFieldType.STRING).description("스터디 참가 대기 유저 닉네임"),
                                fieldWithPath("[].image").type(JsonFieldType.OBJECT).description("스터디 참가 대기 유저 이미지"),
                                fieldWithPath("[].image.thumbnailImage").type(JsonFieldType.STRING).description("스터디 참가 대기 유저 썸네일 이미지 URL"),
                                fieldWithPath("[].image.profileImage").type(JsonFieldType.STRING).description("스터디 참가 대기 유저 이미지 URL"),
                                fieldWithPath("[].gender").type(JsonFieldType.STRING).description("스터디 참가 대기 유저 성별"),
                                fieldWithPath("[].ageRange").type(JsonFieldType.STRING).description("스터디 참가 대기 유저 나이대")
                        )
                ));
        then(studyService).should(times(1)).findWaitUsersByStudyId(any());
    }

    @Test
    @DisplayName("스터디 참가 인원 추가 API 테스트")
    void createStudyUser() throws Exception {
        willDoNothing()
                .given(studyService)
                .createStudyUser(any(), any(), any());

        given(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/studies/{studyId}/users/{userId}", 1, 2)
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

        then(studyService).should(times(1)).createStudyUser(any(), any(), any());
    }

    @Test
    @DisplayName("스터디 참가 신청 거부 API 테스트")
    void deleteWaitUser() throws Exception {
        willDoNothing()
                .given(studyService)
                .deleteWaitUser(any(), any(), any());

        given(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);

        mockMvc.perform(RestDocumentationRequestBuilders
                .delete("/studies/{studyId}/waitUsers/{userId}", 1, 2)
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION))
                .andExpect(status().isOk())
                .andDo(document("study/waitUser/delete",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        pathParameters(
                                parameterWithName("studyId").description("스터디 ID"),
                                parameterWithName("userId").description("스터디 참가 대기 목록의 회원 ID")
                        )
                ));

        then(studyService).should(times(1)).deleteWaitUser(any(), any(), any());
    }

    @Test
    @DisplayName("스터디 참가 인원 조회 API 테스트")
    void findStudyUsersByStudyId() throws Exception {
        List<StudyUserResponse> studyUserResponseList = Arrays.asList(TEST_STUDY_USER_RESPONSE1, TEST_STUDY_USER_RESPONSE2);
        given(studyService.findStudyUsersByStudyId(any()))
                .willReturn(studyUserResponseList);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/studies/{studyId}/users", 1)
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(studyUserResponseList)))
                .andDo(document("study/studyUser/find",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        pathParameters(
                                parameterWithName("studyId").description("스터디 ID")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("스터디 참가 ID"),
                                fieldWithPath("[].userId").type(JsonFieldType.NUMBER).description("스터디 참가 유저 ID"),
                                fieldWithPath("[].nickName").type(JsonFieldType.STRING).description("스터디 참가 유저 닉네임"),
                                fieldWithPath("[].image").type(JsonFieldType.OBJECT).description("스터디 참가 유저 이미지"),
                                fieldWithPath("[].image.thumbnailImage").type(JsonFieldType.STRING).description("스터디 참가 유저 썸네일 이미지 URL"),
                                fieldWithPath("[].image.profileImage").type(JsonFieldType.STRING).description("스터디 참가  유저 이미지 URL"),
                                fieldWithPath("[].gender").type(JsonFieldType.STRING).description("스터디 참가 유저 성별"),
                                fieldWithPath("[].ageRange").type(JsonFieldType.STRING).description("스터디 참가 유저 나이대"),
                                fieldWithPath("[].role").type(JsonFieldType.STRING).description("스터디 참가 유저 권한")
                        )
                ));
        then(studyService).should(times(1)).findStudyUsersByStudyId(any());
    }

    @Test
    @DisplayName("스터디 참가 회원 삭제 API 테스트")
    void deleteStudyUser() throws Exception {
        willDoNothing()
                .given(studyService)
                .deleteStudyUser(any(), any(), any());

        given(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);

        mockMvc.perform(RestDocumentationRequestBuilders
                .delete("/studies/{studyId}/users/{userId}", 1, 2)
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION))
                .andExpect(status().isOk())
                .andDo(document("study/studyUser/delete",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        pathParameters(
                                parameterWithName("studyId").description("스터디 ID"),
                                parameterWithName("userId").description("스터디 참가 회원 목록의 회원 ID")
                        )
                ));

        then(studyService).should(times(1)).deleteStudyUser(any(), any(), any());
    }

    @Test
    @DisplayName("스터디 탈퇴 API 테스트")
    void deleteStudyUserSelf() throws Exception {
        willDoNothing()
                .given(studyService)
                .deleteStudyUserSelf(any(), any());

        given(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);

        mockMvc.perform(RestDocumentationRequestBuilders
                .delete("/studies/{studyId}/users", 2)
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION))
                .andExpect(status().isOk())
                .andDo(document("study/studyUser/deleteSelf",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        pathParameters(
                                parameterWithName("studyId").description("스터디 ID")
                        )
                ));

        then(studyService).should(times(1)).deleteStudyUserSelf(any(), any());
    }

    @Test
    @DisplayName("스터디 목록 조회 API 테스트")
    void findByIdIn() throws Exception {
        List<StudyResponse> studyResponses = Arrays.asList(TEST_STUDY_RESPONSE3, TEST_STUDY_RESPONSE4);

        given(studyService.findByIdIn(any()))
                .willReturn(studyResponses);

        mockMvc.perform(get("/studies/name")
                .accept(MediaType.APPLICATION_JSON)
                .param("studyIdList", "1", "2"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(studyResponses)))
                .andDo(document("study/findName",
                        requestParameters(
                                parameterWithName("studyIdList").description("스터디 ID 리스트")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("스터디 ID"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("스터디 이름"),
                                fieldWithPath("[].numberOfPeople").type(JsonFieldType.NUMBER).description("스터디 인원"),
                                fieldWithPath("[].currentNumberOfPeople").type(JsonFieldType.NUMBER).description("스터디 현재 인원"),
                                fieldWithPath("[].content").type(JsonFieldType.STRING).description("스터디 내용"),
                                fieldWithPath("[].online").type(JsonFieldType.BOOLEAN).description("스터디 온라인 여부"),
                                fieldWithPath("[].offline").type(JsonFieldType.BOOLEAN).description("스터디 오프라인여부"),
                                fieldWithPath("[].status").type(JsonFieldType.STRING).description("스터디 상태"),
                                fieldWithPath("[].image").type(JsonFieldType.OBJECT).description("스터디 이미지"),
                                fieldWithPath("[].image.studyImage").type(JsonFieldType.STRING).description("스터디 이미지 URL"),
                                fieldWithPath("[].image.thumbnailImage").type(JsonFieldType.STRING).description("스터디 썸네일 이미지 URL")
                        )
                ));

        then(studyService).should(times(1)).findByIdIn(any());
    }

    @Test
    @DisplayName("스터디 검색 API 테스트")
    void search() throws Exception {
        List<StudyResponse> content = new ArrayList<>();
        content.add(TEST_STUDY_RESPONSE5);
        content.add(TEST_STUDY_RESPONSE6);
        Pageable pageable = PageRequest.of(0, 20);
        Page<StudyResponse> result = new PageImpl<>(content,pageable,content.size());

        given(studyService.find(any(),any(),any()))
                .willReturn(result);

        mockMvc.perform(get("/studies")
                .header(HttpHeaders.AUTHORIZATION,TEST_AUTHORIZATION)
                .accept(MediaType.APPLICATION_JSON)
                .param("page","0")
                .param("size","20")
                .param("offline","true")
                .param("online","true")
                .param("searchKeyword","스터디")
                .param("categoryId","1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)))
                .andDo(document("study/search",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).optional().description("Access Token")
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("페이지 사이즈"),
                                parameterWithName("offline").description("오프라인 여부"),
                                parameterWithName("online").description("온라인 여부"),
                                parameterWithName("searchKeyword").description("스터디 검색 키워드"),
                                parameterWithName("categoryId").description("카테고리 ID")
                        ),
                        responseFields(
                                fieldWithPath("content").type(JsonFieldType.ARRAY).description("조회 결과 배열"),
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("스터디 ID"),
                                fieldWithPath("content.[].name").type(JsonFieldType.STRING).description("스터디 이름"),
                                fieldWithPath("content.[].numberOfPeople").type(JsonFieldType.NUMBER).description("스터디 인원"),
                                fieldWithPath("content.[].currentNumberOfPeople").type(JsonFieldType.NUMBER).description("스터디 현재 인원"),
                                fieldWithPath("content.[].content").type(JsonFieldType.STRING).description("스터디 내용"),
                                fieldWithPath("content.[].online").type(JsonFieldType.BOOLEAN).description("스터디 온라인 여부"),
                                fieldWithPath("content.[].offline").type(JsonFieldType.BOOLEAN).description("스터디 오프라인여부"),
                                fieldWithPath("content.[].status").type(JsonFieldType.STRING).description("스터디 상태"),
                                fieldWithPath("content.[].image").type(JsonFieldType.OBJECT).description("스터디 이미지"),
                                fieldWithPath("content.[].image.studyImage").type(JsonFieldType.STRING).description("스터디 이미지 URL"),
                                fieldWithPath("content.[].image.thumbnailImage").type(JsonFieldType.STRING).description("스터디 썸네일 이미지 URL"),
                                fieldWithPath("content.[].studyTags").type(JsonFieldType.ARRAY).description("스터디 태그"),
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

        then(studyService).should(times(1)).find(any(),any(),any());
    }

    @Test
    @DisplayName("회원이 가입한 스터디 조회 API 테스트")
    void findByUser() throws Exception {
        List<StudyResponse> studyResponses = Arrays.asList(TEST_STUDY_RESPONSE5, TEST_STUDY_RESPONSE6);

        given(studyService.findByUser(any()))
                .willReturn(studyResponses);

        mockMvc.perform(get("/users/studies")
                .header(HttpHeaders.AUTHORIZATION,TEST_AUTHORIZATION)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(studyResponses)))
                .andDo(document("study/user",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).optional().description("Access Token")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("스터디 ID"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("스터디 이름"),
                                fieldWithPath("[].numberOfPeople").type(JsonFieldType.NUMBER).description("스터디 인원"),
                                fieldWithPath("[].currentNumberOfPeople").type(JsonFieldType.NUMBER).description("스터디 현재 인원"),
                                fieldWithPath("[].content").type(JsonFieldType.STRING).description("스터디 내용"),
                                fieldWithPath("[].online").type(JsonFieldType.BOOLEAN).description("스터디 온라인 여부"),
                                fieldWithPath("[].offline").type(JsonFieldType.BOOLEAN).description("스터디 오프라인여부"),
                                fieldWithPath("[].status").type(JsonFieldType.STRING).description("스터디 상태"),
                                fieldWithPath("[].image").type(JsonFieldType.OBJECT).description("스터디 이미지"),
                                fieldWithPath("[].image.studyImage").type(JsonFieldType.STRING).description("스터디 이미지 URL"),
                                fieldWithPath("[].image.thumbnailImage").type(JsonFieldType.STRING).description("스터디 썸네일 이미지 URL"),
                                fieldWithPath("[].studyTags").type(JsonFieldType.ARRAY).description("스터디 태그")
                        )
                ));

        then(studyService).should(times(1)).findByUser(any());
    }

    @Test
    @DisplayName("스터디 참가 신청 취소 API 테스트")
    void deleteWaitUserSelf() throws Exception {
        willDoNothing()
                .given(studyService)
                .deleteWaitUserSelf(any(), any());

        given(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);

        mockMvc.perform(RestDocumentationRequestBuilders
                .delete("/studies/{studyId}/waitUsers", 1)
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION))
                .andExpect(status().isOk())
                .andDo(document("study/waitUser/cancel",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        pathParameters(
                                parameterWithName("studyId").description("스터디 ID")
                        )
                ));

        then(studyService).should(times(1)).deleteWaitUserSelf(any(), any());
    }


}