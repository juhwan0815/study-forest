package com.study.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.client.UserResponse;
import com.study.dto.chatroom.ChatRoomResponse;
import com.study.dto.study.StudyResponse;
import com.study.dto.studyuser.StudyUserResponse;
import com.study.service.StudyService;
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

import static com.study.CommonFixture.*;
import static com.study.StudyFixture.*;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(StudyController.class)
class StudyControllerTest {

    @MockBean
    private StudyService studyService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(WebApplicationContext wac, RestDocumentationContextProvider restDocumentationContextProvider) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .alwaysDo(print())
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .apply(documentationConfiguration(restDocumentationContextProvider)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();
    }

    @Test
    @DisplayName("프로필 이미지 URL 생성 API")
    void convertToImageUrl() throws Exception {
        // given
        Map<String, String> result = new HashMap<>();
        result.put("imageUrl", TEST_USER_IMAGE_URL);

        given(studyService.uploadImage(any()))
                .willReturn(TEST_USER_IMAGE_URL);

        // when
        mockMvc.perform(multipart("/studies/imageUrls").file(TEST_IMAGE_FILE)
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)))
                .andDo(document("study/imageUrls",
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
        then(studyService).should(times(1)).uploadImage(any());
    }

    @Test
    @DisplayName("프로필 이미지 URL 생성 API 예외")
    void convertToImageUrlNotExist() throws Exception {
        // when
        mockMvc.perform(multipart("/studies/imageUrls").file(TEST_EMPTY_IMAGE_FILE)
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("스터디 생성 API")
    void create() throws Exception {
        // given
        Map<String, Long> response = new HashMap<>();
        response.put("studyId", TEST_STUDY.getId());

        given(studyService.create(any(), any()))
                .willReturn(TEST_STUDY.getId());

        // when
        mockMvc.perform(post("/studies")
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(TEST_STUDY_CREATE_REQUEST)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(document("study/create",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("스터디 이름"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("스터디 내용"),
                                fieldWithPath("numberOfPeople").type(JsonFieldType.NUMBER).description("스터디 인원"),
                                fieldWithPath("online").type(JsonFieldType.BOOLEAN).description("스터디 온라인 여부"),
                                fieldWithPath("offline").type(JsonFieldType.BOOLEAN).description("스터디 오프라인여부"),
                                fieldWithPath("tags").type(JsonFieldType.ARRAY).description("스터디 주제(태그)"),
                                fieldWithPath("imageUrl").type(JsonFieldType.STRING).description("스터디 이미지"),
                                fieldWithPath("areaCode").type(JsonFieldType.STRING).description("지역 코드"),
                                fieldWithPath("categoryId").type(JsonFieldType.NUMBER).description("카테고리 ID")
                        ),
                        responseFields(
                                fieldWithPath("studyId").type(JsonFieldType.NUMBER).description("스터디 ID")
                        )
                ));

        // then
        then(studyService).should(times(1)).create(any(), any());
    }

    @Test
    @DisplayName("스터디 수정 API")
    void update() throws Exception {
        // given
        willDoNothing()
                .given(studyService)
                .update(any(), any(), any());

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.patch("/studies/{studyId}", TEST_STUDY.getId())
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(TEST_STUDY_UPDATE_REQUEST)))
                .andExpect(status().isOk())
                .andDo(document("study/update",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("studyId").description("스터디 ID")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("스터디 이름"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("스터디 내용"),
                                fieldWithPath("numberOfPeople").type(JsonFieldType.NUMBER).description("스터디 인원"),
                                fieldWithPath("online").type(JsonFieldType.BOOLEAN).description("스터디 온라인 여부"),
                                fieldWithPath("offline").type(JsonFieldType.BOOLEAN).description("스터디 오프라인여부"),
                                fieldWithPath("tags").type(JsonFieldType.ARRAY).description("스터디 주제(태그)"),
                                fieldWithPath("open").type(JsonFieldType.BOOLEAN).description("스터디 오픈 여부"),
                                fieldWithPath("imageUrl").type(JsonFieldType.STRING).description("스터디 이미지"),
                                fieldWithPath("areaCode").type(JsonFieldType.STRING).description("지역 코드"),
                                fieldWithPath("categoryId").type(JsonFieldType.NUMBER).description("카테고리 ID")
                        )
                ));

        // then
        then(studyService).should(times(1)).update(any(), any(), any());
    }

    @Test
    @DisplayName("스터디 삭제 API")
    void delete() throws Exception {
        // given
        willDoNothing()
                .given(studyService)
                .delete(any(), any());

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/studies/{studyId}", TEST_STUDY.getId())
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION))
                .andExpect(status().isOk())
                .andDo(document("study/delete",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("studyId").description("스터디 ID")
                        )
                ));

        // then
        then(studyService).should(times(1)).delete(any(), any());
    }

    @Test
    @DisplayName("스터디 단건 조회 API")
    void findById() throws Exception {
        // given
        given(studyService.findById(any()))
                .willReturn(TEST_STUDY_RESPONSE);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/studies/{studyId}", TEST_STUDY.getId())
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_STUDY_RESPONSE)))
                .andDo(document("study/findById",
                        pathParameters(
                                parameterWithName("studyId").description("스터디 ID")
                        ),
                        responseFields(
                                fieldWithPath("studyId").type(JsonFieldType.NUMBER).description("스터디 ID"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("스터디 이름"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("스터디 내용"),
                                fieldWithPath("numberOfPeople").type(JsonFieldType.NUMBER).description("스터디 인원"),
                                fieldWithPath("currentNumberOfPeople").type(JsonFieldType.NUMBER).description("스터디 현재 인원"),
                                fieldWithPath("online").type(JsonFieldType.BOOLEAN).description("스터디 온라인 여부"),
                                fieldWithPath("offline").type(JsonFieldType.BOOLEAN).description("스터디 오프라인여부"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("스터디 상태"),
                                fieldWithPath("imageUrl").type(JsonFieldType.STRING).description("스터디 이미지 URL"),
                                fieldWithPath("area").type(JsonFieldType.OBJECT).description("지역 정보"),
                                fieldWithPath("area.id").type(JsonFieldType.NUMBER).description("지역 ID"),
                                fieldWithPath("area.code").type(JsonFieldType.STRING).description("지역 코드"),
                                fieldWithPath("area.city").type(JsonFieldType.STRING).description("지역 시/도"),
                                fieldWithPath("area.gu").type(JsonFieldType.STRING).description("지역 시/군/구"),
                                fieldWithPath("area.dong").type(JsonFieldType.STRING).description("지역 읍/면/동"),
                                fieldWithPath("area.ri").type(JsonFieldType.STRING).description("지역 --리"),
                                fieldWithPath("area.let").type(JsonFieldType.NUMBER).description("지역 위도"),
                                fieldWithPath("area.len").type(JsonFieldType.NUMBER).description("지역 경도"),
                                fieldWithPath("area.codeType").type(JsonFieldType.STRING).description("지역 코드 타입"),
                                fieldWithPath("parentCategory").type(JsonFieldType.OBJECT).description("상위 카테고리"),
                                fieldWithPath("parentCategory.categoryId").type(JsonFieldType.NUMBER).description("부모 카테고리 ID"),
                                fieldWithPath("parentCategory.name").type(JsonFieldType.STRING).description("부모 카테고리 이름"),
                                fieldWithPath("childCategory").type(JsonFieldType.OBJECT).description("하위 카테고리"),
                                fieldWithPath("childCategory.categoryId").type(JsonFieldType.NUMBER).description("자식 카테고리 ID"),
                                fieldWithPath("childCategory.name").type(JsonFieldType.STRING).description("자식 카테고리 이름"),
                                fieldWithPath("tags").type(JsonFieldType.ARRAY).description("스터디 태그")
                        )));

        // then
        then(studyService).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("스터디 검색 API")
    void search() throws Exception {
        // given
        List<StudyResponse> result = Arrays.asList(TEST_STUDY_RESPONSE3);

        given(studyService.search(any(), any()))
                .willReturn(result);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/studies")
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .param("size", String.valueOf(TEST_STUDY_SEARCH_REQUEST.getSize()))
                        .param("offline", String.valueOf(TEST_STUDY_SEARCH_REQUEST.getOffline()))
                        .param("online", String.valueOf(TEST_STUDY_SEARCH_REQUEST.getOnline()))
                        .param("keyword", TEST_STUDY_SEARCH_REQUEST.getKeyword())
                        .param("categoryId", String.valueOf(TEST_STUDY_SEARCH_REQUEST.getCategoryId()))
                        .param("studyId", String.valueOf(TEST_STUDY_SEARCH_REQUEST.getStudyId())))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)))
                .andDo(document("study/search",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        requestParameters(
                                parameterWithName("size").description("페이지 사이즈"),
                                parameterWithName("offline").description("오프라인 여부"),
                                parameterWithName("online").description("온라인 여부"),
                                parameterWithName("keyword").description("검색 키워드"),
                                parameterWithName("categoryId").description("카테고리 ID"),
                                parameterWithName("studyId").description("스터디 ID")
                        ),
                        responseFields(
                                fieldWithPath("[].studyId").type(JsonFieldType.NUMBER).description("스터디 ID"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("스터디 이름"),
                                fieldWithPath("[].numberOfPeople").type(JsonFieldType.NUMBER).description("스터디 인원"),
                                fieldWithPath("[].currentNumberOfPeople").type(JsonFieldType.NUMBER).description("스터디 현재 인원"),
                                fieldWithPath("[].online").type(JsonFieldType.BOOLEAN).description("스터디 온라인 여부"),
                                fieldWithPath("[].offline").type(JsonFieldType.BOOLEAN).description("스터디 오프라인여부"),
                                fieldWithPath("[].status").type(JsonFieldType.STRING).description("스터디 상태"),
                                fieldWithPath("[].imageUrl").type(JsonFieldType.STRING).description("스터디 이미지 URL"),
                                fieldWithPath("[].tags").type(JsonFieldType.ARRAY).description("스터디 태그")
                        )));

        // then
        then(studyService).should(times(1)).search(any(), any());
    }

    @Test
    @DisplayName("스터디 참가 신청 API")
    void createWaitUser() throws Exception {
        // given
        willDoNothing()
                .given(studyService)
                .createWaitUser(any(), any());

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.post("/studies/{studyId}/waitUsers", TEST_STUDY.getId())
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION))
                .andExpect(status().isOk())
                .andDo(document("study/waitUsers/create",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("studyId").description("스터디 ID")
                        )
                ));

        // then
        then(studyService).should(times(1)).createWaitUser(any(), any());
    }

    @Test
    @DisplayName("스터디 침기 신청 취소 API")
    void deleteWaitUser() throws Exception {
        // given
        willDoNothing()
                .given(studyService)
                .deleteWaitUser(any(), any());

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/studies/{studyId}/waitUsers", TEST_STUDY.getId())
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION))
                .andExpect(status().isOk())
                .andDo(document("study/waitUsers/delete",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("studyId").description("스터디 ID")
                        )
                ));

        // then
        then(studyService).should(times(1)).deleteWaitUser(any(), any());
    }

    @Test
    @DisplayName("스터디 참가 거부 API")
    void failWaitUser() throws Exception {
        // given
        willDoNothing()
                .given(studyService)
                .failWaitUser(any(), any(), any());

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/studies/{studyId}/waitUsers/{waitUserId}", TEST_STUDY.getId(), 1L)
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION))
                .andExpect(status().isOk())
                .andDo(document("study/waitUsers/fail",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("studyId").description("스터디 ID"),
                                parameterWithName("waitUserId").description("회원 ID")
                        )
                ));

        // then
        then(studyService).should(times(1)).failWaitUser(any(), any(), any());
    }

    @Test
    @DisplayName("스터디 참가 대기자 조회 API")
    void findWaitUsersById() throws Exception {
        // given
        List<UserResponse> result = Arrays.asList(TEST_USER_RESPONSE);

        given(studyService.findWaitUsersById(any()))
                .willReturn(result);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/studies/{studyId}/waitUsers", TEST_STUDY.getId())
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)))
                .andDo(document("study/waitUsers/find",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("studyId").description("스터디 ID")
                        ),
                        responseFields(
                                fieldWithPath("[].userId").type(JsonFieldType.NUMBER).description("회원 ID"),
                                fieldWithPath("[].nickName").type(JsonFieldType.STRING).description("회원 닉네임"),
                                fieldWithPath("[].ageRange").type(JsonFieldType.STRING).description("회원 나이대"),
                                fieldWithPath("[].gender").type(JsonFieldType.STRING).description("회원 성별"),
                                fieldWithPath("[].imageUrl").type(JsonFieldType.STRING).description("회원 이미지 URL"),
                                fieldWithPath("[].areaId").type(JsonFieldType.NUMBER).description("회원 지역 ID"),
                                fieldWithPath("[].distance").type(JsonFieldType.NUMBER).description("회원 검색 거리"),
                                fieldWithPath("[].fcmToken").type(JsonFieldType.STRING).description("회원 FCM 토큰")
                        )
                ));

        // then
        then(studyService).should(times(1)).findWaitUsersById(any());
    }

    @Test
    @DisplayName("스터디 참가자 생성 API")
    void createStudyUser() throws Exception {
        // given
        willDoNothing()
                .given(studyService)
                .createStudyUser(any(), any(), any());

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.post("/studies/{studyId}/studyUsers/{studyUserId}", TEST_STUDY.getId(), 1L)
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION))
                .andExpect(status().isOk())
                .andDo(document("study/studyUsers/create",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("studyId").description("스터디 ID"),
                                parameterWithName("studyUserId").description("회원 ID")
                        )
                ));

        // then
        then(studyService).should(times(1)).createStudyUser(any(), any(), any());
    }

    @Test
    @DisplayName("스터디 참가자 방출 API")
    void deleteStudyUser() throws Exception {
        // given
        willDoNothing()
                .given(studyService)
                .deleteStudyUser(any(), any(), any());

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/studies/{studyId}/studyUsers/{studyUserId}", TEST_STUDY.getId(), 1L)
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION))
                .andExpect(status().isOk())
                .andDo(document("study/studyUsers/delete",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("studyId").description("스터디 ID"),
                                parameterWithName("studyUserId").description("회원 ID")
                        )
                ));

        // then
        then(studyService).should(times(1)).deleteStudyUser(any(), any(), any());
    }

    @Test
    @DisplayName("스터디 탈퇴 API")
    void deleteStudyUserSelf() throws Exception {
        // given
        willDoNothing()
                .given(studyService)
                .deleteStudyUser(any(), any());

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/studies/{studyId}/studyUsers", TEST_STUDY.getId())
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION))
                .andExpect(status().isOk())
                .andDo(document("study/studyUsers/deleteSelf",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("studyId").description("스터디 ID")
                        )
                ));

        // then
        then(studyService).should(times(1)).deleteStudyUser(any(), any());
    }

    @Test
    @DisplayName("스터디 참가자 조회 API")
    void findStudyUsersById() throws Exception {
        // given
        List<StudyUserResponse> result = Arrays.asList(TEST_STUDY_USER_RESPONSE);

        given(studyService.findStudyUsersById(any()))
                .willReturn(result);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/studies/{studyId}/studyUsers", TEST_STUDY.getId())
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)))
                .andDo(document("study/studyUsers/find",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("studyId").description("스터디 ID")
                        ),
                        responseFields(
                                fieldWithPath("[].userId").type(JsonFieldType.NUMBER).description("회원 ID"),
                                fieldWithPath("[].studyRole").type(JsonFieldType.STRING).description("회원 스터디 권한"),
                                fieldWithPath("[].nickName").type(JsonFieldType.STRING).description("회원 닉네임"),
                                fieldWithPath("[].ageRange").type(JsonFieldType.STRING).description("회원 나이대"),
                                fieldWithPath("[].gender").type(JsonFieldType.STRING).description("회원 성별"),
                                fieldWithPath("[].imageUrl").type(JsonFieldType.STRING).description("회원 이미지 URL"),
                                fieldWithPath("[].fcmToken").type(JsonFieldType.STRING).description("회원 FCM 토큰")
                        )
                ));

        // then
        then(studyService).should(times(1)).findStudyUsersById(any());
    }

    @Test
    @DisplayName("스터디 채팅방 생성 API")
    void createChatRoom() throws Exception {
        // given
        willDoNothing()
                .given(studyService)
                .createChatRoom(any(), any(), any());

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.post("/studies/{studyId}/chatRooms", TEST_STUDY.getId())
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(TEST_CHAT_ROOM_CREATE_REQUEST)))
                .andExpect(status().isOk())
                .andDo(document("study/chatRooms/create",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("studyId").description("스터디 ID")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("채팅방 이름")
                        )
                ));

        // then
        then(studyService).should(times(1)).createChatRoom(any(), any(), any());
    }

    @Test
    @DisplayName("스터디 채팅방 수정 API")
    void updateChatRoom() throws Exception {
        // given
        willDoNothing()
                .given(studyService)
                .updateChatRoom(any(), any(), any(), any());

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.put("/studies/{studyId}/chatRooms/{chatRoomId}", TEST_STUDY.getId(), 1L)
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(TEST_CHAT_ROOM_UPDATE_REQUEST)))
                .andExpect(status().isOk())
                .andDo(document("study/chatRooms/update",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("studyId").description("스터디 ID"),
                                parameterWithName("chatRoomId").description("채팅방 ID")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("채팅방 이름")
                        )
                ));

        // then
        then(studyService).should(times(1)).updateChatRoom(any(), any(), any(), any());
    }

    @Test
    @DisplayName("채팅방 삭제 API")
    void deleteChatRoom() throws Exception {
        // given
        willDoNothing()
                .given(studyService)
                .deleteChatRoom(any(), any(), any());

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/studies/{studyId}/chatRooms/{chatRoomId}", TEST_STUDY.getId(), 1L)
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION))
                .andExpect(status().isOk())
                .andDo(document("study/chatRooms/delete",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("studyId").description("스터디 ID"),
                                parameterWithName("chatRoomId").description("채팅방 ID")
                        )
                ));

        // then
        then(studyService).should(times(1)).deleteChatRoom(any(), any(), any());
    }

    @Test
    @DisplayName("스터디 채팅방 조회 API")
    void findChatRoomsById() throws Exception {
        // given
        List<ChatRoomResponse> result = Arrays.asList(TEST_CHAT_ROOM_RESPONSE);

        given(studyService.findChatRoomsById(any()))
                .willReturn(result);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/studies/{studyId}/chatRooms", TEST_STUDY.getId())
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)))
                .andDo(document("study/chatRooms/find",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("studyId").description("스터디 ID")
                        ),
                        responseFields(
                                fieldWithPath("[].chatRoomId").type(JsonFieldType.NUMBER).description("채팅방 ID"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("채팅방 이름")
                        )
                ));

        // then
        then(studyService).should(times(1)).findChatRoomsById(any());
    }

    @Test
    @DisplayName("회원의 스터디 조회 API")
    void findByUserId() throws Exception {
        // given
        List<StudyResponse> result = Arrays.asList(TEST_STUDY_RESPONSE3);

        // given
        given(studyService.findByUserId(any()))
                .willReturn(result);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/users/studies")
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)))
                .andDo(document("study/users",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        responseFields(
                                fieldWithPath("[].studyId").type(JsonFieldType.NUMBER).description("스터디 ID"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("스터디 이름"),
                                fieldWithPath("[].numberOfPeople").type(JsonFieldType.NUMBER).description("스터디 인원"),
                                fieldWithPath("[].currentNumberOfPeople").type(JsonFieldType.NUMBER).description("스터디 현재 인원"),
                                fieldWithPath("[].online").type(JsonFieldType.BOOLEAN).description("스터디 온라인 여부"),
                                fieldWithPath("[].offline").type(JsonFieldType.BOOLEAN).description("스터디 오프라인여부"),
                                fieldWithPath("[].status").type(JsonFieldType.STRING).description("스터디 상태"),
                                fieldWithPath("[].imageUrl").type(JsonFieldType.STRING).description("스터디 이미지 URL"),
                                fieldWithPath("[].tags").type(JsonFieldType.ARRAY).description("스터디 태그"))));

        // then
        then(studyService).should(times(1)).findByUserId(any());

    }

    @Test
    @DisplayName("회원의 스터디 참가 신청 조회 API")
    void findByWaitUserId() throws Exception {
        // given
        List<StudyResponse> result = Arrays.asList(TEST_STUDY_RESPONSE3);

        // given
        given(studyService.findByWaitUserId(any()))
                .willReturn(result);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/studies/waitUsers")
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)))
                .andDo(document("study/waitUsers",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        responseFields(
                                fieldWithPath("[].studyId").type(JsonFieldType.NUMBER).description("스터디 ID"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("스터디 이름"),
                                fieldWithPath("[].numberOfPeople").type(JsonFieldType.NUMBER).description("스터디 인원"),
                                fieldWithPath("[].currentNumberOfPeople").type(JsonFieldType.NUMBER).description("스터디 현재 인원"),
                                fieldWithPath("[].online").type(JsonFieldType.BOOLEAN).description("스터디 온라인 여부"),
                                fieldWithPath("[].offline").type(JsonFieldType.BOOLEAN).description("스터디 오프라인여부"),
                                fieldWithPath("[].status").type(JsonFieldType.STRING).description("스터디 상태"),
                                fieldWithPath("[].imageUrl").type(JsonFieldType.STRING).description("스터디 이미지 URL"),
                                fieldWithPath("[].tags").type(JsonFieldType.ARRAY).description("스터디 태그"))));

        // then
        then(studyService).should(times(1)).findByWaitUserId(any());
    }

    @Test
    @DisplayName("채팅방 ID 로 스터디 조회 API")
    void findByChatRoomId() throws Exception {
        // given
        given(studyService.findByChatRoomId(any()))
                .willReturn(TEST_STUDY_RESPONSE2);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/studies/chatRooms/{chatRoomId}", 1L)
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_STUDY_RESPONSE2)))
                .andDo(document("study/findByChatRoomId",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("chatRoomId").description("채팅방 ID")
                        ),
                        responseFields(
                                fieldWithPath("studyId").type(JsonFieldType.NUMBER).description("스터디 ID"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("스터디 이름"),
                                fieldWithPath("numberOfPeople").type(JsonFieldType.NUMBER).description("스터디 인원"),
                                fieldWithPath("currentNumberOfPeople").type(JsonFieldType.NUMBER).description("스터디 현재 인원"),
                                fieldWithPath("online").type(JsonFieldType.BOOLEAN).description("스터디 온라인 여부"),
                                fieldWithPath("offline").type(JsonFieldType.BOOLEAN).description("스터디 오프라인여부"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("스터디 상태"),
                                fieldWithPath("imageUrl").type(JsonFieldType.STRING).description("스터디 이미지 URL")
                        )
                ));

        // then
        then(studyService).should(times(1)).findByChatRoomId(any());
    }

    @Test
    @DisplayName("채팅방 단건 조회 API")
    void findChatRoomByIdAndChatRoomId() throws Exception {
        // given
        given(studyService.findChatRoomByIdAndChatRoomId(any(), any()))
                .willReturn(TEST_CHAT_ROOM_RESPONSE);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/studies/{studyId}/chatRooms/{chatRoomId}", TEST_STUDY.getId(), 1L)
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_CHAT_ROOM_RESPONSE)))
                .andDo(document("study/chatRooms/findById",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("studyId").description("스터디 ID"),
                                parameterWithName("chatRoomId").description("채팅방 ID")
                        ),
                        responseFields(
                                fieldWithPath("chatRoomId").type(JsonFieldType.NUMBER).description("채팅방 ID"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("채팅방 이름")
                        )
                ));

        // then
        then(studyService).should(times(1)).findChatRoomByIdAndChatRoomId(any(), any());
    }

}