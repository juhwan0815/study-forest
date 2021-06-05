package com.study.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.userservice.domain.UserRole;
import com.study.userservice.domain.UserStatus;
import com.study.userservice.model.UserLoginRequest;
import com.study.userservice.model.UserResponse;
import com.study.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(UserController.class)
class UserControllerTest {

    @MockBean
    private UserService userService;

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
        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setKakaoId(1L);
        userLoginRequest.setNickName("황주환");
        userLoginRequest.setProfileImage("이미지");
        userLoginRequest.setThumbnailImage("이미지");

        UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setKakaoId(userLoginRequest.getKakaoId());
        userResponse.setNickName(userLoginRequest.getNickName());
        userResponse.setProfileImage(userLoginRequest.getProfileImage());
        userResponse.setThumbnailImage(userLoginRequest.getThumbnailImage());
        userResponse.setRole(UserRole.USER);
        userResponse.setStatus(UserStatus.ACTIVE);

        given(userService.userLogin(any()))
                .willReturn(userResponse);

        // when
        mockMvc.perform(post("/login")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(userLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.kakaoId").value(1))
                .andExpect(jsonPath("$.nickName").value("황주환"))
                .andExpect(jsonPath("$.profileImage").value("이미지"))
                .andExpect(jsonPath("$.thumbnailImage").value("이미지"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andDo(document("user/login",
                        requestFields(
                                fieldWithPath("kakaoId").type(JsonFieldType.NUMBER).description("카카오 ID"),
                                fieldWithPath("nickName").type(JsonFieldType.STRING).description("카카오 닉네임"),
                                fieldWithPath("profileImage").type(JsonFieldType.STRING).description("카카오 프로필 이미지"),
                                fieldWithPath("thumbnailImage").type(JsonFieldType.STRING).description("카카오 프로필 썸네일 이미지")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("회원 ID"),
                                fieldWithPath("kakaoId").type(JsonFieldType.NUMBER).description("카카오 ID"),
                                fieldWithPath("nickName").type(JsonFieldType.STRING).description("카카오 닉네임"),
                                fieldWithPath("profileImage").type(JsonFieldType.STRING).description("카카오 프로필 이미지"),
                                fieldWithPath("thumbnailImage").type(JsonFieldType.STRING).description("카카오 프로필 썸네일 이미지"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("회원 상태"),
                                fieldWithPath("role").type(JsonFieldType.STRING).description("회원 권한")
                        )));

        // then
        then(userService).should(times(1)).userLogin(any());
    }
}