package com.study.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.config.LoginUserArgumentResolver;
import com.study.dto.TokenResponse;
import com.study.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    private static final String TEST_AUTHORIZATION = "bearer **" ;

    @MockBean
    private AuthService authService;

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
    @DisplayName("소셜 로그인 API 테스트")
    void login() throws Exception {

        TokenResponse tokenResponse = new TokenResponse("accessToken", "refreshToken");

        given(authService.login(any(), any()))
                .willReturn(tokenResponse);

        mockMvc.perform(post("/social/login")
                        .header("kakaoToken", "kakaoToken")
                        .header("fcmToken", "fcmToken"))
                .andExpect(status().isOk())
                .andExpect(header().string("accessToken", "accessToken"))
                .andExpect(header().string("refreshToken", "refreshToken"))
                .andDo(document("auth/social/login",
                        requestHeaders(
                                headerWithName("kakaoToken").description("kakaoToken"),
                                headerWithName("fcmToken").description("fcmToken")
                        ),
                        responseHeaders(
                                headerWithName("accessToken").description("AccessToken"),
                                headerWithName("refreshToken").description("RefreshToken")
                        )));

        then(authService).should(times(1)).login(any(), any());
    }

    @Test
    @DisplayName("토큰 재발급 API 테스트")
    void refresh() throws Exception {

        TokenResponse tokenResponse = new TokenResponse("accessToken", "refreshToken");

        given(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);

        given(authService.refresh(any(), any()))
                .willReturn(tokenResponse);


        mockMvc.perform(post("/refresh")
                        .header("Authorization", TEST_AUTHORIZATION))
                .andExpect(status().isOk())
                .andExpect(header().string("accessToken", "accessToken"))
                .andExpect(header().string("refreshToken", "refreshToken"))
                .andDo(document("auth/refresh",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("RefreshToken")
                        ),
                        responseHeaders(
                                headerWithName("accessToken").description("AccessToken"),
                                headerWithName("refreshToken").description("RefreshToken")
                        )));

        then(authService).should(times(1)).refresh(any(), any());
    }


}