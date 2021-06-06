package com.study.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.head;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    private final String TEST_AUTHORIZATION = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiUk9MRSI6IlVTRVIiLCJpYXQiOjE2MjI4ODU3NDEsImV4cCI6MTYyMzQ5MDU0MX0.c24V3JQxYlp9L4XgtFqfL6KR31CuTNRC5i-M0t8nMAU";

    @MockBean
    private AuthService authService;

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

        given(authService.refresh(any()))
                .willReturn(createTokenResult);

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

        then(authService).should(times(1)).refresh(any());
    }

}