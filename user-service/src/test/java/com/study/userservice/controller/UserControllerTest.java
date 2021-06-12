package com.study.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.userservice.config.LoginUserArgumentResolver;
import com.study.userservice.domain.UserRole;
import com.study.userservice.domain.UserStatus;
import com.study.userservice.model.UserLoginRequest;
import com.study.userservice.model.UserProfileUpdateRequest;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

        given(userService.save(any()))
                .willReturn(userResponse);

        // when
        mockMvc.perform(post("/users")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
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
                .andDo(document("user/create",
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
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
        then(userService).should(times(1)).save(any());
    }

    @Test
    @DisplayName("회원 조회 (Refresh 포함) API 테스트")
    void findUserWithRefreshTokenById() throws Exception {

        // given
        UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setKakaoId(1L);
        userResponse.setNickName("황주환");
        userResponse.setProfileImage("이미지");
        userResponse.setThumbnailImage("이미지");
        userResponse.setRefreshToken(TEST_AUTHORIZATION);
        userResponse.setRole(UserRole.USER);
        userResponse.setStatus(UserStatus.ACTIVE);

        given(userService.findWithRefreshTokenById(any()))
                .willReturn(userResponse);

        // when
        mockMvc.perform(get("/users/{userId}/auth", 1)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.kakaoId").value(1))
                .andExpect(jsonPath("$.nickName").value("황주환"))
                .andExpect(jsonPath("$.profileImage").value("이미지"))
                .andExpect(jsonPath("$.thumbnailImage").value("이미지"))
                .andExpect(jsonPath("$.refreshToken").value(TEST_AUTHORIZATION))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andDo(document("user/auth",
                        requestHeaders(
                                headerWithName("Authorization").description("Access Token")
                        ),
                        pathParameters(
                                parameterWithName("userId").description("회원 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("회원 ID"),
                                fieldWithPath("kakaoId").type(JsonFieldType.NUMBER).description("카카오 ID"),
                                fieldWithPath("nickName").type(JsonFieldType.STRING).description("카카오 닉네임"),
                                fieldWithPath("profileImage").type(JsonFieldType.STRING).description("카카오 프로필 이미지"),
                                fieldWithPath("thumbnailImage").type(JsonFieldType.STRING).description("카카오 프로필 썸네일 이미지"),
                                fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("Refresh토큰"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("회원 상태"),
                                fieldWithPath("role").type(JsonFieldType.STRING).description("회원 권한")
                        )));

        then(userService).should(times(1)).findWithRefreshTokenById(any());
    }

    @Test
    @DisplayName("프로필 수정API 테스트")
    void profileUpdate() throws Exception {
        // given
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "프로필사진.png",
                MediaType.IMAGE_PNG_VALUE,
                "<<image>>".getBytes(StandardCharsets.UTF_8));

        UserProfileUpdateRequest userProfileUpdateRequest = new UserProfileUpdateRequest();
        userProfileUpdateRequest.setDeleteImage(false);
        userProfileUpdateRequest.setNickName("황철원");

        MockMultipartFile request = new MockMultipartFile("request",
                "request"
                , MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(userProfileUpdateRequest).getBytes(StandardCharsets.UTF_8));

        UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setKakaoId(1L);
        userResponse.setNickName("황철원");
        userResponse.setProfileImage("이미지URL");
        userResponse.setThumbnailImage("썸네일이미지URL");
        userResponse.setRole(UserRole.USER);
        userResponse.setStatus(UserStatus.ACTIVE);

        given(userService.profileUpdate(any(), any(), any()))
                .willReturn(userResponse);

        given(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);

        MockMultipartHttpServletRequestBuilder builder =
                RestDocumentationRequestBuilders.fileUpload("/users/profile");

        builder.with(request1 -> {
            request1.setMethod("PATCH");
            return request1;
        });

        // when
        mockMvc.perform(builder.file(image).file(request)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userResponse)))
                .andDo(document("user/profile",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        requestParts(
                                partWithName("image").description("변경할 이미지 파일").optional(),
                                partWithName("request").ignored()
                        ),
                        requestPartFields("request",
                                fieldWithPath("deleteImage").type(JsonFieldType.BOOLEAN).description("이미지 삭제 여부"),
                                fieldWithPath("nickName").type(JsonFieldType.STRING).description("변경할 회원 닉네임")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("회원 ID"),
                                fieldWithPath("kakaoId").type(JsonFieldType.NUMBER).description("카카오 ID"),
                                fieldWithPath("nickName").type(JsonFieldType.STRING).description("변경된 회원 닉네임"),
                                fieldWithPath("profileImage").type(JsonFieldType.STRING).description("변경된 프로필 이미지 URL"),
                                fieldWithPath("thumbnailImage").type(JsonFieldType.STRING).description("변경된 프로필 썸네일 이미지 URL"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("회원 상태"),
                                fieldWithPath("role").type(JsonFieldType.STRING).description("회원 권한")
                        )));

        // then
        then(userService).should(times(1)).profileUpdate(any
                (), any(), any());
    }

}