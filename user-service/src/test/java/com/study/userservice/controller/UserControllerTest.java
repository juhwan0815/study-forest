package com.study.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.userservice.config.LoginUserArgumentResolver;
import com.study.userservice.model.user.UserLoginRequest;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static com.study.userservice.UserFixture.*;
import static org.mockito.ArgumentMatchers.any;
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
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
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
                                fieldWithPath("ageRange").type(JsonFieldType.STRING).description("회원 나이대")
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
                                fieldWithPath("numberOfStudyApply").type(JsonFieldType.NUMBER).description("스터디 신청 내역 수")
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
                RestDocumentationRequestBuilders.fileUpload("/users/profile");
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
                                fieldWithPath("numberOfStudyApply").type(JsonFieldType.NUMBER).description("스터디 신청 내역 수")
                        )));

        // then
        then(userService).should(times(1)).updateProfile(any(), any(), any());
    }

}