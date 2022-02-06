package com.study.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.config.LoginUserArgumentResolver;
import com.study.dto.keyword.KeywordResponse;
import com.study.dto.user.UserResponse;
import com.study.service.UserService;
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.study.UserFixture.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(UserController.class)
class UserControllerTest {

    private final String TEST_AUTHORIZATION = "Bearer *****";
    private final String TEST_KAKAO_TOKEN = "kakaoToken";
    private final String TEST_FCM_TOKEN = "fcmToken";

    @MockBean
    private UserService userService;

    @MockBean
    private LoginUserArgumentResolver loginUserArgumentResolver;

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
    @DisplayName("회원 가입 API")
    void create() throws Exception {
        // given
        willDoNothing()
                .given(userService)
                .create(any());

        // when
        mockMvc.perform(post("/users")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header(TEST_KAKAO_TOKEN, TEST_KAKAO_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("user/create",
                        requestHeaders(
                                headerWithName("kakaoToken").description("kakaoToken")
                        )));

        // then
        then(userService).should(times(1)).create(any());
    }

    @Test
    @DisplayName("회원 로그인 API")
    void login() throws Exception {
        // given
        given(userService.findByKakaoId(any(), any()))
                .willReturn(TEST_USER_RESPONSE);

        // when
        mockMvc.perform(post("/users/{kakaoId}", TEST_USER.getKakaoId())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header(TEST_FCM_TOKEN, TEST_FCM_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_USER_RESPONSE)))
                .andDo(document("user/login",
                        pathParameters(
                                parameterWithName("kakaoId").description("카카오 ID")
                        ),
                        requestHeaders(
                                headerWithName("fcmToken").description("FCM 토큰")
                        ),
                        responseFields(
                                fieldWithPath("userId").type(JsonFieldType.NUMBER).description("회원 ID"),
                                fieldWithPath("nickName").type(JsonFieldType.STRING).description("회원 닉네임"),
                                fieldWithPath("ageRange").type(JsonFieldType.STRING).description("회원 나이대"),
                                fieldWithPath("gender").type(JsonFieldType.STRING).description("회원 성별"),
                                fieldWithPath("imageUrl").type(JsonFieldType.STRING).description("회원 프로필 이미지"),
                                fieldWithPath("areaId").type(JsonFieldType.NUMBER).description("회원 지역 ID"),
                                fieldWithPath("distance").type(JsonFieldType.NUMBER).description("회원 검색 거리"),
                                fieldWithPath("fcmToken").type(JsonFieldType.STRING).description("회원 FCM 토큰")
                        )));

        // then
        then(userService).should(times(1)).findByKakaoId(any(), any());
    }

    @Test
    @DisplayName("회원 조회 API")
    void findById() throws Exception {
        // given
        given(userService.findById(any()))
                .willReturn(TEST_USER_RESPONSE);
        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/users/{userId}", TEST_USER.getId())
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_USER_RESPONSE)))
                .andDo(document("user/findById",
                        pathParameters(
                                parameterWithName("userId").description("회원 ID")
                        ),
                        responseFields(
                                fieldWithPath("userId").type(JsonFieldType.NUMBER).description("회원 ID"),
                                fieldWithPath("nickName").type(JsonFieldType.STRING).description("회원 닉네임"),
                                fieldWithPath("ageRange").type(JsonFieldType.STRING).description("회원 나이대"),
                                fieldWithPath("gender").type(JsonFieldType.STRING).description("회원 성별"),
                                fieldWithPath("imageUrl").type(JsonFieldType.STRING).description("회원 프로필 이미지"),
                                fieldWithPath("areaId").type(JsonFieldType.NUMBER).description("회원 지역 ID"),
                                fieldWithPath("distance").type(JsonFieldType.NUMBER).description("회원 검색 거리"),
                                fieldWithPath("fcmToken").type(JsonFieldType.STRING).description("회원 FCM 토큰")
                        )));

        // then
        then(userService).should(times(1)).findById(any());
    }


    @Test
    @DisplayName("회원 ID 리스트 조회 API")
    void findByIdIn() throws Exception {
        // given
        List<UserResponse> result = Arrays.asList(TEST_USER_RESPONSE);

        given(userService.findByIdIn(any()))
                .willReturn(result);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/users")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .param("userIds", "1", "2"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)))
                .andDo(document("user/findByIdIn",
                        requestParameters(
                                parameterWithName("userIds").description("회원 ID 리스트")
                        ),
                        responseFields(
                                fieldWithPath("[].userId").type(JsonFieldType.NUMBER).description("회원 ID"),
                                fieldWithPath("[].nickName").type(JsonFieldType.STRING).description("회원 닉네임"),
                                fieldWithPath("[].ageRange").type(JsonFieldType.STRING).description("회원 나이대"),
                                fieldWithPath("[].gender").type(JsonFieldType.STRING).description("회원 성별"),
                                fieldWithPath("[].imageUrl").type(JsonFieldType.STRING).description("회원 프로필 이미지"),
                                fieldWithPath("[].areaId").type(JsonFieldType.NUMBER).description("회원 지역 ID"),
                                fieldWithPath("[].distance").type(JsonFieldType.NUMBER).description("회원 검색 거리"),
                                fieldWithPath("[].fcmToken").type(JsonFieldType.STRING).description("회원 FCM 토큰")
                        )));

        // then
        then(userService).should(times(1)).findByIdIn(any());
    }

    @Test
    @DisplayName("회원 프로필 수정 API")
    void update() throws Exception {
        // given
        willDoNothing()
                .given(userService)
                .update(any(), any());

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.patch("/users/profile")
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TEST_USER_UPDATE_REQUEST)))
                .andExpect(status().isOk())
                .andDo(document("user/update/profile",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        requestFields(
                                fieldWithPath("nickName").type(JsonFieldType.STRING).description("회원 닉네임"),
                                fieldWithPath("imageUrl").type(JsonFieldType.STRING).description("변경할 이미지 URL")
                        )));

        // then
        then(userService).should(times(1)).update(any(), any());
    }

    @Test
    @DisplayName("회원 프로필 조회 API")
    void findByLoginId() throws Exception {
        // given
        given(userService.findById(any()))
                .willReturn(TEST_USER_RESPONSE);
        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/users/profile")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_USER_RESPONSE)))
                .andDo(document("user/findByLoginId",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        responseFields(
                                fieldWithPath("userId").type(JsonFieldType.NUMBER).description("회원 ID"),
                                fieldWithPath("nickName").type(JsonFieldType.STRING).description("회원 닉네임"),
                                fieldWithPath("ageRange").type(JsonFieldType.STRING).description("회원 나이대"),
                                fieldWithPath("gender").type(JsonFieldType.STRING).description("회원 성별"),
                                fieldWithPath("imageUrl").type(JsonFieldType.STRING).description("회원 프로필 이미지"),
                                fieldWithPath("areaId").type(JsonFieldType.NUMBER).description("회원 지역 ID"),
                                fieldWithPath("distance").type(JsonFieldType.NUMBER).description("회원 검색 거리"),
                                fieldWithPath("fcmToken").type(JsonFieldType.STRING).description("회원 FCM 토큰")
                        )));

        // then
        then(userService).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("회원 지역 수정 API")
    void updateArea() throws Exception {
        // given
        willDoNothing()
                .given(userService)
                .updateArea(any(), any());

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.patch("/users/areas/{areaId}", TEST_USER_AREA_ID)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION))
                .andExpect(status().isOk())
                .andDo(document("user/update/area",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("areaId").description("지역 ID")
                        )));

        // then
        then(userService).should(times(1)).updateArea(any(), any());
    }

    @Test
    @DisplayName("회원 검색 거리 수정 API")
    void updateDistance() throws Exception {
        // given
        willDoNothing()
                .given(userService)
                .updateDistance(any(), any());

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.patch("/users/distance")
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TEST_USER_UPDATE_DISTANCE_REQUEST)))
                .andExpect(status().isOk())
                .andDo(document("user/update/distance",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        requestFields(
                                fieldWithPath("distance").type(JsonFieldType.NUMBER).description("검색 거리")
                        )));

        // then
        then(userService).should(times(1)).updateDistance(any(), any());
    }

    @Test
    @DisplayName("회원 관심 키워드 추가 API")
    void addKeyword() throws Exception {
        // given
        willDoNothing()
                .given(userService)
                .addKeyword(any(), any());

        // when
        mockMvc.perform(post("/users/keywords")
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TEST_KEYWORD_CREATE_REQUEST)))
                .andExpect(status().isOk())
                .andDo(document("user/keyword/create",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        requestFields(
                                fieldWithPath("content").type(JsonFieldType.STRING).description("키워드 내용")
                        )));

        // then
        then(userService).should(times(1)).addKeyword(any(), any());
    }

    @Test
    @DisplayName("회원 관심 키워드 삭제 API")
    void deleteKeyword() throws Exception {
        // given
        willDoNothing()
                .given(userService)
                .deleteKeyword(any(), any());

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/users/keywords/{keywordId}", TEST_KEYWORD.getId())
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION))
                .andExpect(status().isOk())
                .andDo(document("user/keyword/delete",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("keywordId").description("키워드 ID")
                        )));

        // then
        then(userService).should(times(1)).deleteKeyword(any(), any());
    }

    @Test
    @DisplayName("회원 관심 키워드 조회 API")
    void findKeywordById() throws Exception {
        // given
        List<KeywordResponse> result = Arrays.asList(TEST_KEYWORD_RESPONSE);
        given(userService.findKeywordById(any()))
                .willReturn(result);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/users/keywords")
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("user/keyword/find",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        responseFields(
                                fieldWithPath("[].keywordId").type(JsonFieldType.NUMBER).description("키워드 ID"),
                                fieldWithPath("[].content").type(JsonFieldType.STRING).description("키워드 내용")
                        )
                ));

        // then
        then(userService).should(times(1)).findKeywordById(any());
    }

    @Test
    @DisplayName("키워드를 가진 회원 조회 API")
    void findByKeywordContentContain() throws Exception {
        // given
        List<UserResponse> result = Arrays.asList(TEST_USER_RESPONSE);
        given(userService.findByKeywordContentContain(any()))
                .willReturn(result);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/users/keywords/notifications")
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("content", "스프링"))
                .andExpect(status().isOk())
                .andDo(document("user/keyword/findByKeywordContentContain",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        requestParameters(
                                parameterWithName("content").description("검색 키워드 내용")
                        ),
                        responseFields(
                                fieldWithPath("[].userId").type(JsonFieldType.NUMBER).description("회원 ID"),
                                fieldWithPath("[].nickName").type(JsonFieldType.STRING).description("회원 닉네임"),
                                fieldWithPath("[].ageRange").type(JsonFieldType.STRING).description("회원 나이대"),
                                fieldWithPath("[].gender").type(JsonFieldType.STRING).description("회원 성별"),
                                fieldWithPath("[].imageUrl").type(JsonFieldType.STRING).description("회원 프로필 이미지"),
                                fieldWithPath("[].areaId").type(JsonFieldType.NUMBER).description("회원 지역 ID"),
                                fieldWithPath("[].distance").type(JsonFieldType.NUMBER).description("회원 검색 거리"),
                                fieldWithPath("[].fcmToken").type(JsonFieldType.STRING).description("회원 FCM 토큰")
                        )
                ));

        // then
        then(userService).should(times(1)).findByKeywordContentContain(any());
    }

    @Test
    @DisplayName("프로필 이미지 URL 생성 API")
    void convertToImageUrl() throws Exception {
        // given
        Map<String, String> result = new HashMap<>();
        result.put("imageUrl", TEST_USER_IMAGE_URL);

        given(userService.uploadImage(any()))
                .willReturn(TEST_USER_IMAGE_URL);

        // when
        mockMvc.perform(multipart("/api/users/imageUrls").file(TEST_IMAGE_FILE)
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)))
                .andDo(document("user/imageUrls",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        requestParts(
                                partWithName("image").description("이미지 파일")
                        ),
                        responseFields(
                                fieldWithPath("imageUrl").type(JsonFieldType.STRING).description("이미지 URL")
                        )
                ));

        // then
        then(userService).should(times(1)).uploadImage(any());
    }

    @Test
    @DisplayName("프로필 이미지 URL 생성 API 예외")
    void convertToImageUrlNotExist() throws Exception {
        // when
        mockMvc.perform(multipart("/api/users/imageUrls").file(TEST_EMPTY_IMAGE_FILE)
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is4xxClientError());
    }

}