package com.study.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.authservice.config.LoginUserArgumentResolver;
import com.study.authservice.model.CreateTokenResult;
import com.study.authservice.service.AuthService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
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

    private final String TEST_AUTHORIZATION = "Bearer 토큰";

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
                .apply(documentationConfiguration(restDocumentationContextProvider)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();
    }

    @Test
    @DisplayName("로그인 테스트")
    void login() throws Exception {

        String accessToken = Jwts.builder()
                .setSubject(String.valueOf(1L))
                .claim("ROLE", "USER")
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + 86400000L))
                .signWith(SignatureAlgorithm.HS256, "study")
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(String.valueOf(1L))
                .claim("ROLE", "USER")
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + 604800000L))
                .signWith(SignatureAlgorithm.HS256, "study")
                .compact();

        CreateTokenResult createTokenResult = new CreateTokenResult();
        createTokenResult.setAccessToken(accessToken);
        createTokenResult.setRefreshToken(refreshToken);

        given(authService.login(any()))
                .willReturn(createTokenResult);

        mockMvc.perform(post("/login")
                .header("kakaoToken", "kakaoToken"))
                .andExpect(status().isOk())
                .andExpect(header().string("accessToken", createTokenResult.getAccessToken()))
                .andExpect(header().string("refreshToken", createTokenResult.getRefreshToken()))
                .andDo(document("auth/login",
                        requestHeaders(
                                headerWithName("kakaoToken").description("카카오 토큰")
                        ),
                        responseHeaders(
                                headerWithName("accessToken").description("Access 토큰"),
                                headerWithName("refreshToken").description("Refresh 토큰")
                        )));

        then(authService).should(times(1)).login(any());
    }

    @Test
    @DisplayName("토큰 재발급 API 테스트")
    void refreshToken() throws Exception {

        CreateTokenResult createTokenResult = new CreateTokenResult();
        createTokenResult.setAccessToken("Access토큰");
        createTokenResult.setRefreshToken("Refresh토큰");

        given(authService.refresh(any(), any()))
                .willReturn(createTokenResult);

        given(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);

        mockMvc.perform(post("/refresh")
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION))
                .andExpect(status().isOk())
                .andExpect(header().string("accessToken", createTokenResult.getAccessToken()))
                .andExpect(header().string("refreshToken", createTokenResult.getRefreshToken()))
                .andDo(document("auth/refresh",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Refresh Token")
                        ),
                        responseHeaders(
                                headerWithName("accessToken").description("새로 발급된 Access 토큰"),
                                headerWithName("refreshToken").description("새로 발급된 Refresh 토큰")
                        )));

        then(authService).should(times(1)).refresh(any(), any());
    }

    @Test
    @DisplayName("로그아웃 API 테스트")
    void logout() throws Exception {

        // given
        given(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);

        willDoNothing()
                .given(authService)
                .logout(any());

        // when
        mockMvc.perform(post("/logout")
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION))
                .andExpect(status().isOk())
                .andDo(document("auth/logout",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        )));

        then(authService).should(times(1)).logout(any());
    }
}