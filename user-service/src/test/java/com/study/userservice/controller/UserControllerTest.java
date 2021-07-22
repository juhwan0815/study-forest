package com.study.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.userservice.config.LoginUserArgumentResolver;
import com.study.userservice.model.interestTag.InterestTagResponse;
import com.study.userservice.model.studyapply.StudyApplyResponse;
import com.study.userservice.model.user.UserLoginRequest;
import com.study.userservice.model.user.UserResponse;
import com.study.userservice.model.user.UserWithTagResponse;
import com.study.userservice.service.UserService;
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

import static com.study.userservice.UserFixture.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
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
@WebMvcTest(UserController.class)
class UserControllerTest {

    private final String TEST_AUTHORIZATION = "Bearer 액세스토큰";

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
    @DisplayName("회원 로그인 API 테스트")
    void loginApi() throws Exception {
        // given
        given(userService.create((UserLoginRequest) any()))
                .willReturn(TEST_USER_RESPONSE);

        // when
        mockMvc.perform(post("/users")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(TEST_USER_LOGIN_REQUEST)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_USER_RESPONSE)))
                .andDo(document("user/create",
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        requestFields(
                                fieldWithPath("kakaoId").type(JsonFieldType.NUMBER).description("카카오 ID"),
                                fieldWithPath("nickName").type(JsonFieldType.STRING).description("카카오 닉네임"),
                                fieldWithPath("profileImage").type(JsonFieldType.STRING).description("카카오 프로필 이미지"),
                                fieldWithPath("thumbnailImage").type(JsonFieldType.STRING).description("카카오 프로필 썸네일 이미지"),
                                fieldWithPath("gender").type(JsonFieldType.STRING).description("회원 성별"),
                                fieldWithPath("ageRange").type(JsonFieldType.STRING).description("회원 나이대"),
                                fieldWithPath("fcmToken").type(JsonFieldType.STRING).description("FCM 토큰")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("회원 ID"),
                                fieldWithPath("kakaoId").type(JsonFieldType.NUMBER).description("카카오 ID"),
                                fieldWithPath("nickName").type(JsonFieldType.STRING).description("카카오 닉네임"),
                                fieldWithPath("image").type(JsonFieldType.OBJECT).description("이미지 정보"),
                                fieldWithPath("image.profileImage").type(JsonFieldType.STRING).description("프로필 이미지 URL "),
                                fieldWithPath("image.thumbnailImage").type(JsonFieldType.STRING).description("썸네일 이미지 URL"),
                                fieldWithPath("gender").type(JsonFieldType.STRING).description("회원 성별"),
                                fieldWithPath("ageRange").type(JsonFieldType.STRING).description("회원 나이대"),
                                fieldWithPath("numberOfStudyApply").type(JsonFieldType.NUMBER).description("스터디 신청 내역 수"),
                                fieldWithPath("locationId").type(JsonFieldType.NUMBER).description("회원 지역 정보 ID"),
                                fieldWithPath("searchDistance").type(JsonFieldType.NUMBER).description("회원 오프라인 검색 거리"),
                                fieldWithPath("role").type(JsonFieldType.STRING).description("회원 권한")
                        )));

        // then
        then(userService).should(times(1)).create((UserLoginRequest) any());
    }


    @Test
    @DisplayName("프로필 수정 API 테스트")
    void profileUpdate() throws Exception {
        // given
        MockMultipartFile request = new MockMultipartFile("request",
                "request"
                , MediaType.APPLICATION_JSON_VALUE,
                objectMapper
                        .writeValueAsString(TEST_USER_PROFILE_UPDATE_REQUEST1)
                        .getBytes(StandardCharsets.UTF_8));


        MockMultipartHttpServletRequestBuilder builder =
                fileUpload("/users/profile");
        builder.with(request1 -> {
            request1.setMethod("PATCH");
            return request1;
        });

        given(userService.updateProfile(any(), any(), any()))
                .willReturn(TEST_USER_RESPONSE2);

        given(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);

        // when
        mockMvc.perform(builder.file(TEST_IMAGE_FILE).file(request)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_USER_RESPONSE2)))
                .andDo(document("user/update/profile",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        requestParts(
                                partWithName("image").description("변경할 이미지 파일").optional(),
                                partWithName("request").description("변경할 내용")
                        ),
                        requestPartFields("request",
                                fieldWithPath("deleteImage").type(JsonFieldType.BOOLEAN).description("이미지 삭제 여부"),
                                fieldWithPath("nickName").type(JsonFieldType.STRING).description("변경할 회원 닉네임")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("회원 ID"),
                                fieldWithPath("kakaoId").type(JsonFieldType.NUMBER).description("카카오 ID"),
                                fieldWithPath("nickName").type(JsonFieldType.STRING).description("변경된 회원 닉네임"),
                                fieldWithPath("image").type(JsonFieldType.OBJECT).description("변경된 이미지 정보"),
                                fieldWithPath("image.profileImage").type(JsonFieldType.STRING).description("변경된 프로필 이미지 URL"),
                                fieldWithPath("image.thumbnailImage").type(JsonFieldType.STRING).description("변경된 프로필 썸네일 이미지 URL"),
                                fieldWithPath("gender").type(JsonFieldType.STRING).description("회원 성별"),
                                fieldWithPath("ageRange").type(JsonFieldType.STRING).description("회원 나이대"),
                                fieldWithPath("numberOfStudyApply").type(JsonFieldType.NUMBER).description("스터디 신청 내역 수"),
                                fieldWithPath("locationId").type(JsonFieldType.NUMBER).description("회원 지역 정보 ID"),
                                fieldWithPath("searchDistance").type(JsonFieldType.NUMBER).description("회원 오프라인 검색 거리"),
                                fieldWithPath("role").type(JsonFieldType.STRING).description("회원 권한")
                        )));

        // then
        then(userService).should(times(1)).updateProfile(any(), any(), any());
    }


    @Test
    @DisplayName("로그인 회원 정보 조회 API 테스트")
    void findByLoginUserId() throws Exception {
        given(userService.findById(any()))
                .willReturn(TEST_USER_RESPONSE);

        mockMvc.perform(get("/users/profile")
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_USER_RESPONSE)))
                .andDo(document("user/findByLoginId",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access 토큰")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("회원 ID"),
                                fieldWithPath("kakaoId").type(JsonFieldType.NUMBER).description("카카오 ID"),
                                fieldWithPath("nickName").type(JsonFieldType.STRING).description("회원 닉네임"),
                                fieldWithPath("image").type(JsonFieldType.OBJECT).description("회원 이미지 정보"),
                                fieldWithPath("image.profileImage").type(JsonFieldType.STRING).description("회원 프로필 이미지 URL"),
                                fieldWithPath("image.thumbnailImage").type(JsonFieldType.STRING).description("회원 프로필 썸네일 이미지 URL"),
                                fieldWithPath("gender").type(JsonFieldType.STRING).description("회원 성별"),
                                fieldWithPath("ageRange").type(JsonFieldType.STRING).description("회원 나이대"),
                                fieldWithPath("numberOfStudyApply").type(JsonFieldType.NUMBER).description("스터디 신청 내역 수"),
                                fieldWithPath("locationId").type(JsonFieldType.NUMBER).description("회원 지역 정보 ID"),
                                fieldWithPath("searchDistance").type(JsonFieldType.NUMBER).description("회원 오프라인 검색 거리"),
                                fieldWithPath("role").type(JsonFieldType.STRING).description("회원 권한")
                        )
                ));
    }

    @Test
    @DisplayName("회원 ID 조회 API 테스트")
    void findById() throws Exception {
        given(userService.findById(anyLong()))
                .willReturn(TEST_USER_RESPONSE);

        mockMvc.perform(get("/users/{userId}", 1)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_USER_RESPONSE)))
                .andDo(document("user/findById",
                        pathParameters(
                                parameterWithName("userId").description("조회할 회원 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("회원 ID"),
                                fieldWithPath("kakaoId").type(JsonFieldType.NUMBER).description("카카오 ID"),
                                fieldWithPath("nickName").type(JsonFieldType.STRING).description("회원 닉네임"),
                                fieldWithPath("image").type(JsonFieldType.OBJECT).description("회원 이미지 정보"),
                                fieldWithPath("image.profileImage").type(JsonFieldType.STRING).description("회원 프로필 이미지 URL"),
                                fieldWithPath("image.thumbnailImage").type(JsonFieldType.STRING).description("회원 프로필 썸네일 이미지 URL"),
                                fieldWithPath("gender").type(JsonFieldType.STRING).description("회원 성별"),
                                fieldWithPath("ageRange").type(JsonFieldType.STRING).description("회원 나이대"),
                                fieldWithPath("numberOfStudyApply").type(JsonFieldType.NUMBER).description("스터디 신청 내역 수"),
                                fieldWithPath("locationId").type(JsonFieldType.NUMBER).description("회원 지역 정보 ID"),
                                fieldWithPath("searchDistance").type(JsonFieldType.NUMBER).description("회원 오프라인 검색 거리"),
                                fieldWithPath("role").type(JsonFieldType.STRING).description("회원 권한")
                        )
                ));

        then(userService).should(times(1)).findById(any());
    }


    @Test
    @DisplayName("회원 탈퇴 API 테스트")
    void delete() throws Exception {

        willDoNothing()
                .given(userService)
                .delete(any());

        given(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/users")
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION))
                .andExpect(status().isOk())
                .andDo(document("user/delete",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        )
                ));

        then(userService).should(times(1)).delete(any());
    }

    @Test
    @DisplayName("회원 목록 조회 API 테스트")
    void findUsers() throws Exception {
        // given
        List<UserResponse> userResponses = Arrays.asList(TEST_USER_RESPONSE3, TEST_USER_RESPONSE4);

        given(userService.findByIdIn(any()))
                .willReturn(userResponses);

        // when
        mockMvc.perform(get("/users")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .param("userIdList", "1", "2"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userResponses)))
                .andDo(document("user/findByIdIn",
                        requestParameters(
                                parameterWithName("userIdList").description("회원 ID 배열")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("회원 ID"),
                                fieldWithPath("[].kakaoId").type(JsonFieldType.NUMBER).description("카카오 ID"),
                                fieldWithPath("[].nickName").type(JsonFieldType.STRING).description("회원 닉네임"),
                                fieldWithPath("[].image").type(JsonFieldType.OBJECT).description("회원 이미지 정보"),
                                fieldWithPath("[].image.profileImage").type(JsonFieldType.STRING).description("회원 프로필 이미지 URL"),
                                fieldWithPath("[].image.thumbnailImage").type(JsonFieldType.STRING).description("회원 프로필 썸네일 이미지 URL"),
                                fieldWithPath("[].gender").type(JsonFieldType.STRING).description("회원 성별"),
                                fieldWithPath("[].ageRange").type(JsonFieldType.STRING).description("회원 나이대"),
                                fieldWithPath("[].numberOfStudyApply").type(JsonFieldType.NUMBER).description("스터디 신청 내역 수"),
                                fieldWithPath("[].locationId").type(JsonFieldType.NUMBER).description("회원 지역 정보 ID"),
                                fieldWithPath("[].searchDistance").type(JsonFieldType.NUMBER).description("회원 오프라인 검색 거리"),
                                fieldWithPath("[].fcmToken").type(JsonFieldType.STRING).description("회원의 FCM 토큰"),
                                fieldWithPath("[].role").type(JsonFieldType.STRING).description("회원 권한")
                        )
                ));
        // then
        then(userService).should(times(1)).findByIdIn(any());
    }

    @Test
    @DisplayName("회원 지역 정보 변경 API")
    void updateLocation() throws Exception {
        // given
        given(userService.updateLocation(any(), any()))
                .willReturn(TEST_USER_RESPONSE2);

        // when
        mockMvc.perform(patch("/users/locations/{locationId}", 2)
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION))
                .andExpect(status().isOk())
                .andDo(document("user/location/update",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access 토큰")
                        ),
                        pathParameters(
                                parameterWithName("locationId").description("변경할 지역정보 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("회원 ID"),
                                fieldWithPath("kakaoId").type(JsonFieldType.NUMBER).description("카카오 ID"),
                                fieldWithPath("nickName").type(JsonFieldType.STRING).description("회원 닉네임"),
                                fieldWithPath("image").type(JsonFieldType.OBJECT).description("회원 이미지 정보"),
                                fieldWithPath("image.profileImage").type(JsonFieldType.STRING).description("회원 프로필 이미지 URL"),
                                fieldWithPath("image.thumbnailImage").type(JsonFieldType.STRING).description("회원 프로필 썸네일 이미지 URL"),
                                fieldWithPath("gender").type(JsonFieldType.STRING).description("회원 성별"),
                                fieldWithPath("ageRange").type(JsonFieldType.STRING).description("회원 나이대"),
                                fieldWithPath("numberOfStudyApply").type(JsonFieldType.NUMBER).description("스터디 신청 내역 수"),
                                fieldWithPath("locationId").type(JsonFieldType.NUMBER).description("회원 지역 정보 ID"),
                                fieldWithPath("searchDistance").type(JsonFieldType.NUMBER).description("회원 오프라인 검색 거리"),
                                fieldWithPath("role").type(JsonFieldType.STRING).description("회원 권한")
                        )
                ));

        // then
        then(userService).should(times(1)).updateLocation(any(), any());
    }

    @Test
    @DisplayName("회원 관심 주제 추가 API")
    void addInterestTag() throws Exception {
        // given
        willDoNothing()
                .given(userService)
                .addInterestTag(any(), any());

        mockMvc.perform(post("/users/tags/{tagId}", 2)
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION))
                .andExpect(status().isOk())
                .andDo(document("user/tag/create",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access 토큰")
                        ),
                        pathParameters(
                                parameterWithName("tagId").description("추가할 태그 ID")
                        )
                ));

        // then
        then(userService).should(times(1)).addInterestTag(any(),any());
    }

    @Test
    @DisplayName("회원 관심 주제 삭제 API 테스트")
    void deleteInterestTag() throws Exception {
        // given
        willDoNothing()
                .given(userService)
                .deleteInterestTag(any(), any());

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/users/tags/{tagId}", 2)
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION))
                .andExpect(status().isOk())
                .andDo(document("user/tag/delete",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access 토큰")
                        ),
                        pathParameters(
                                parameterWithName("tagId").description("삭제할 태그 ID")
                        )
                ));

        // then
        then(userService).should(times(1)).deleteInterestTag(any(),any());
    }

    @Test
    @DisplayName("회원 관심 주제 목록 조회 API 테스트")
    void findInterestTagsByUserId() throws Exception{
        // given
        List<InterestTagResponse> interestTagResponses = Arrays.asList(TEST_INTEREST_TAG_RESPONSE1, TEST_INTEREST_TAG_RESPONSE2);
        given(userService.findInterestTagByUserId(any()))
                .willReturn(interestTagResponses);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/users/tags")
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(interestTagResponses)))
                .andDo(document("user/tag/find",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access 토큰")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("관심 태그 ID"),
                                fieldWithPath("[].tagId").type(JsonFieldType.NUMBER).description("태그 ID"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("태그 이름")
                        )
                ));

        // then
        then(userService).should(times(1)).findInterestTagByUserId(any());
    }

    @Test
    @DisplayName("회원의 스터디 신청 이력 조회 API 테스트")
    void findStudyAppliesByUserId() throws Exception{
        // given
        List<StudyApplyResponse> studyApplyResponses
                = Arrays.asList(TEST_STUDY_APPLY_RESPONSE1, TEST_STUDY_APPLY_RESPONSE2, TEST_STUDY_APPLY_RESPONSE3);
        given(userService.findStudyAppliesByUserId(any()))
                .willReturn(studyApplyResponses);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/users/studyApply")
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(studyApplyResponses)))
                .andDo(document("user/studyApply/find",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access 토큰")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("스터디 신청 내역 ID"),
                                fieldWithPath("[].studyId").type(JsonFieldType.NUMBER).description("스터디 ID"),
                                fieldWithPath("[].studyName").type(JsonFieldType.STRING).description("스터디 이름"),
                                fieldWithPath("[].status").type(JsonFieldType.STRING).description("스터디 신청 내역 상태")
                        )
                ));

        // then
        then(userService).should(times(1)).findStudyAppliesByUserId(any());
    }

    @Test
    @DisplayName("회원의 오프라인 검색거리 수정 API 테스트")
    void updateSearchDistance() throws Exception{

        given(userService.updateSearchDistance(any(),any()))
                .willReturn(TEST_USER_RESPONSE);

        mockMvc.perform(patch("/users/searchDistance/{searchDistance}",2)
                .header(HttpHeaders.AUTHORIZATION,TEST_AUTHORIZATION)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_USER_RESPONSE)))
                .andDo(document("user/searchDistance/update",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access 토큰")
                        ),
                        pathParameters(
                                parameterWithName("searchDistance").description("변경할 오프라인 검색 거리")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("회원 ID"),
                                fieldWithPath("kakaoId").type(JsonFieldType.NUMBER).description("카카오 ID"),
                                fieldWithPath("nickName").type(JsonFieldType.STRING).description("회원 닉네임"),
                                fieldWithPath("image").type(JsonFieldType.OBJECT).description("회원 이미지 정보"),
                                fieldWithPath("image.profileImage").type(JsonFieldType.STRING).description("회원 프로필 이미지 URL"),
                                fieldWithPath("image.thumbnailImage").type(JsonFieldType.STRING).description("회원 프로필 썸네일 이미지 URL"),
                                fieldWithPath("gender").type(JsonFieldType.STRING).description("회원 성별"),
                                fieldWithPath("ageRange").type(JsonFieldType.STRING).description("회원 나이대"),
                                fieldWithPath("numberOfStudyApply").type(JsonFieldType.NUMBER).description("스터디 신청 내역 수"),
                                fieldWithPath("locationId").type(JsonFieldType.NUMBER).description("회원 지역 정보 ID"),
                                fieldWithPath("searchDistance").type(JsonFieldType.NUMBER).description("변경된 회원 오프라인 검색 거리"),
                                fieldWithPath("role").type(JsonFieldType.STRING).description("회원 권한")
                        )
                ));

        then(userService).should(times(1)).updateSearchDistance(any(),any());
    }

    @Test
    @DisplayName("알림서비스용 회원 조회 API 테스트")
    void findWithInterestTagsByTagIdList() throws Exception{
        // given
        List<UserWithTagResponse> result = Arrays.asList(TEST_USER_WITH_TAG_RESPONSE1,TEST_USER_WITH_TAG_RESPONSE2);
        given(userService.findInterestTagByTagIdList(any()))
                .willReturn(result);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/users/interestTags")
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                .param("tagIdList", "1", "2")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)))
                .andDo(document("user/notification",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access 토큰")
                        ),
                        requestParameters(
                                parameterWithName("tagIdList").description("태그 ID 배열")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("회원 ID"),
                                fieldWithPath("[].fcmToken").type(JsonFieldType.STRING).description("회원 Fcm 토큰"),
                                fieldWithPath("[].tags.[].id").type(JsonFieldType.NUMBER).description("회원 관심 태그 ID"),
                                fieldWithPath("[].tags.[].tagId").type(JsonFieldType.NUMBER).description("화원 관심태그 태그ID")
                        )
                ));

        // then
        then(userService).should(times(1)).findInterestTagByTagIdList(any());
    }


}