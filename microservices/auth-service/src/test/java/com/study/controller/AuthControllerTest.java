package com.study.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.AuthFixture;
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

import static com.study.AuthFixture.*;
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

    @MockBean
    private AuthService authService;

    @MockBean
    private LoginUserArgumentResolver loginUserArgumentResolver;

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
        // given
        given(authService.login(any(), any()))
                .willReturn(TEST_TOKEN_RESPONSE);

        // when
        mockMvc.perform(post("/social/login")
                        .header(TEST_KAKAO_TOKEN, TEST_KAKAO_TOKEN)
                        .header(TEST_FCM_TOKEN, TEST_FCM_TOKEN))
                .andExpect(status().isOk())
                .andExpect(header().string(TEST_AUTH_ACCESS_TOKEN, TEST_TOKEN_RESPONSE.getAccessToken()))
                .andExpect(header().string(TEST_AUTH_REFRESH_TOKEN, TEST_TOKEN_RESPONSE.getRefreshToken()))
                .andDo(document("auth/social/login",
                        requestHeaders(
                                headerWithName("kakaoToken").description("kakaoToken"),
                                headerWithName("fcmToken").description("fcmToken")
                        ),
                        responseHeaders(
                                headerWithName("accessToken").description("AccessToken"),
                                headerWithName("refreshToken").description("RefreshToken")
                        )));

        // then
        then(authService).should(times(1)).login(any(), any());
    }

    @Test
    @DisplayName("토큰 재발급 API 테스트")
    void refresh() throws Exception {
        // given
        given(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(TEST_AUTH.getUserId());

        given(authService.refresh(any(), any()))
                .willReturn(TEST_TOKEN_RESPONSE);

        // when
        mockMvc.perform(post("/refresh")
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION))
                .andExpect(status().isOk())
                .andExpect(header().string(TEST_AUTH_ACCESS_TOKEN, TEST_TOKEN_RESPONSE.getAccessToken()))
                .andExpect(header().string(TEST_AUTH_REFRESH_TOKEN, TEST_TOKEN_RESPONSE.getRefreshToken()))
                .andDo(document("auth/refresh",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("RefreshToken")
                        ),
                        responseHeaders(
                                headerWithName("accessToken").description("AccessToken"),
                                headerWithName("refreshToken").description("RefreshToken")
                        )));

        // then
        then(authService).should(times(1)).refresh(any(), any());
    }

}