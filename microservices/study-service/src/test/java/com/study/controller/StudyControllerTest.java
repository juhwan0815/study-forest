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
    @DisplayName("????????? ????????? URL ?????? API")
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
                                partWithName("image").description("????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("imageUrl").type(JsonFieldType.STRING).description("????????? URL")
                        )
                ));

        // then
        then(studyService).should(times(1)).uploadImage(any());
    }

    @Test
    @DisplayName("????????? ????????? URL ?????? API ??????")
    void convertToImageUrlNotExist() throws Exception {
        // when
        mockMvc.perform(multipart("/studies/imageUrls").file(TEST_EMPTY_IMAGE_FILE)
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("????????? ?????? API")
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
                                fieldWithPath("name").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("numberOfPeople").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("online").type(JsonFieldType.BOOLEAN).description("????????? ????????? ??????"),
                                fieldWithPath("offline").type(JsonFieldType.BOOLEAN).description("????????? ??????????????????"),
                                fieldWithPath("tags").type(JsonFieldType.ARRAY).description("????????? ??????(??????)"),
                                fieldWithPath("imageUrl").type(JsonFieldType.STRING).description("????????? ?????????"),
                                fieldWithPath("areaCode").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("categoryId").type(JsonFieldType.NUMBER).description("???????????? ID")
                        ),
                        responseFields(
                                fieldWithPath("studyId").type(JsonFieldType.NUMBER).description("????????? ID")
                        )
                ));

        // then
        then(studyService).should(times(1)).create(any(), any());
    }

    @Test
    @DisplayName("????????? ?????? API")
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
                                parameterWithName("studyId").description("????????? ID")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("numberOfPeople").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("online").type(JsonFieldType.BOOLEAN).description("????????? ????????? ??????"),
                                fieldWithPath("offline").type(JsonFieldType.BOOLEAN).description("????????? ??????????????????"),
                                fieldWithPath("tags").type(JsonFieldType.ARRAY).description("????????? ??????(??????)"),
                                fieldWithPath("open").type(JsonFieldType.BOOLEAN).description("????????? ?????? ??????"),
                                fieldWithPath("imageUrl").type(JsonFieldType.STRING).description("????????? ?????????"),
                                fieldWithPath("areaCode").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("categoryId").type(JsonFieldType.NUMBER).description("???????????? ID")
                        )
                ));

        // then
        then(studyService).should(times(1)).update(any(), any(), any());
    }

    @Test
    @DisplayName("????????? ?????? API")
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
                                parameterWithName("studyId").description("????????? ID")
                        )
                ));

        // then
        then(studyService).should(times(1)).delete(any(), any());
    }

    @Test
    @DisplayName("????????? ?????? ?????? API")
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
                                parameterWithName("studyId").description("????????? ID")
                        ),
                        responseFields(
                                fieldWithPath("studyId").type(JsonFieldType.NUMBER).description("????????? ID"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("numberOfPeople").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("currentNumberOfPeople").type(JsonFieldType.NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("online").type(JsonFieldType.BOOLEAN).description("????????? ????????? ??????"),
                                fieldWithPath("offline").type(JsonFieldType.BOOLEAN).description("????????? ??????????????????"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("imageUrl").type(JsonFieldType.STRING).description("????????? ????????? URL"),
                                fieldWithPath("area").type(JsonFieldType.OBJECT).description("?????? ??????"),
                                fieldWithPath("area.id").type(JsonFieldType.NUMBER).description("?????? ID"),
                                fieldWithPath("area.code").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("area.city").type(JsonFieldType.STRING).description("?????? ???/???"),
                                fieldWithPath("area.gu").type(JsonFieldType.STRING).description("?????? ???/???/???"),
                                fieldWithPath("area.dong").type(JsonFieldType.STRING).description("?????? ???/???/???"),
                                fieldWithPath("area.ri").type(JsonFieldType.STRING).description("?????? --???"),
                                fieldWithPath("area.let").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("area.len").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("area.codeType").type(JsonFieldType.STRING).description("?????? ?????? ??????"),
                                fieldWithPath("parentCategory").type(JsonFieldType.OBJECT).description("?????? ????????????"),
                                fieldWithPath("parentCategory.categoryId").type(JsonFieldType.NUMBER).description("?????? ???????????? ID"),
                                fieldWithPath("parentCategory.name").type(JsonFieldType.STRING).description("?????? ???????????? ??????"),
                                fieldWithPath("childCategory").type(JsonFieldType.OBJECT).description("?????? ????????????"),
                                fieldWithPath("childCategory.categoryId").type(JsonFieldType.NUMBER).description("?????? ???????????? ID"),
                                fieldWithPath("childCategory.name").type(JsonFieldType.STRING).description("?????? ???????????? ??????"),
                                fieldWithPath("tags").type(JsonFieldType.ARRAY).description("????????? ??????")
                        )));

        // then
        then(studyService).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("????????? ?????? API")
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
                                parameterWithName("size").description("????????? ?????????"),
                                parameterWithName("offline").description("???????????? ??????"),
                                parameterWithName("online").description("????????? ??????"),
                                parameterWithName("keyword").description("?????? ?????????"),
                                parameterWithName("categoryId").description("???????????? ID"),
                                parameterWithName("studyId").description("????????? ID")
                        ),
                        responseFields(
                                fieldWithPath("[].studyId").type(JsonFieldType.NUMBER).description("????????? ID"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("[].numberOfPeople").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("[].currentNumberOfPeople").type(JsonFieldType.NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("[].online").type(JsonFieldType.BOOLEAN).description("????????? ????????? ??????"),
                                fieldWithPath("[].offline").type(JsonFieldType.BOOLEAN).description("????????? ??????????????????"),
                                fieldWithPath("[].status").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("[].imageUrl").type(JsonFieldType.STRING).description("????????? ????????? URL"),
                                fieldWithPath("[].tags").type(JsonFieldType.ARRAY).description("????????? ??????")
                        )));

        // then
        then(studyService).should(times(1)).search(any(), any());
    }

    @Test
    @DisplayName("????????? ?????? ?????? API")
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
                                parameterWithName("studyId").description("????????? ID")
                        )
                ));

        // then
        then(studyService).should(times(1)).createWaitUser(any(), any());
    }

    @Test
    @DisplayName("????????? ?????? ?????? ?????? API")
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
                                parameterWithName("studyId").description("????????? ID")
                        )
                ));

        // then
        then(studyService).should(times(1)).deleteWaitUser(any(), any());
    }

    @Test
    @DisplayName("????????? ?????? ?????? API")
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
                                parameterWithName("studyId").description("????????? ID"),
                                parameterWithName("waitUserId").description("?????? ID")
                        )
                ));

        // then
        then(studyService).should(times(1)).failWaitUser(any(), any(), any());
    }

    @Test
    @DisplayName("????????? ?????? ????????? ?????? API")
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
                                parameterWithName("studyId").description("????????? ID")
                        ),
                        responseFields(
                                fieldWithPath("[].userId").type(JsonFieldType.NUMBER).description("?????? ID"),
                                fieldWithPath("[].nickName").type(JsonFieldType.STRING).description("?????? ?????????"),
                                fieldWithPath("[].ageRange").type(JsonFieldType.STRING).description("?????? ?????????"),
                                fieldWithPath("[].gender").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("[].imageUrl").type(JsonFieldType.STRING).description("?????? ????????? URL"),
                                fieldWithPath("[].areaId").type(JsonFieldType.NUMBER).description("?????? ?????? ID"),
                                fieldWithPath("[].distance").type(JsonFieldType.NUMBER).description("?????? ?????? ??????"),
                                fieldWithPath("[].fcmToken").type(JsonFieldType.STRING).description("?????? FCM ??????")
                        )
                ));

        // then
        then(studyService).should(times(1)).findWaitUsersById(any());
    }

    @Test
    @DisplayName("????????? ????????? ?????? API")
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
                                parameterWithName("studyId").description("????????? ID"),
                                parameterWithName("studyUserId").description("?????? ID")
                        )
                ));

        // then
        then(studyService).should(times(1)).createStudyUser(any(), any(), any());
    }

    @Test
    @DisplayName("????????? ????????? ?????? API")
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
                                parameterWithName("studyId").description("????????? ID"),
                                parameterWithName("studyUserId").description("?????? ID")
                        )
                ));

        // then
        then(studyService).should(times(1)).deleteStudyUser(any(), any(), any());
    }

    @Test
    @DisplayName("????????? ?????? API")
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
                                parameterWithName("studyId").description("????????? ID")
                        )
                ));

        // then
        then(studyService).should(times(1)).deleteStudyUser(any(), any());
    }

    @Test
    @DisplayName("????????? ????????? ?????? API")
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
                                parameterWithName("studyId").description("????????? ID")
                        ),
                        responseFields(
                                fieldWithPath("[].userId").type(JsonFieldType.NUMBER).description("?????? ID"),
                                fieldWithPath("[].studyRole").type(JsonFieldType.STRING).description("?????? ????????? ??????"),
                                fieldWithPath("[].nickName").type(JsonFieldType.STRING).description("?????? ?????????"),
                                fieldWithPath("[].ageRange").type(JsonFieldType.STRING).description("?????? ?????????"),
                                fieldWithPath("[].gender").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("[].imageUrl").type(JsonFieldType.STRING).description("?????? ????????? URL"),
                                fieldWithPath("[].fcmToken").type(JsonFieldType.STRING).description("?????? FCM ??????")
                        )
                ));

        // then
        then(studyService).should(times(1)).findStudyUsersById(any());
    }

    @Test
    @DisplayName("????????? ????????? ?????? API")
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
                                parameterWithName("studyId").description("????????? ID")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("????????? ??????")
                        )
                ));

        // then
        then(studyService).should(times(1)).createChatRoom(any(), any(), any());
    }

    @Test
    @DisplayName("????????? ????????? ?????? API")
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
                                parameterWithName("studyId").description("????????? ID"),
                                parameterWithName("chatRoomId").description("????????? ID")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("????????? ??????")
                        )
                ));

        // then
        then(studyService).should(times(1)).updateChatRoom(any(), any(), any(), any());
    }

    @Test
    @DisplayName("????????? ?????? API")
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
                                parameterWithName("studyId").description("????????? ID"),
                                parameterWithName("chatRoomId").description("????????? ID")
                        )
                ));

        // then
        then(studyService).should(times(1)).deleteChatRoom(any(), any(), any());
    }

    @Test
    @DisplayName("????????? ????????? ?????? API")
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
                                parameterWithName("studyId").description("????????? ID")
                        ),
                        responseFields(
                                fieldWithPath("[].chatRoomId").type(JsonFieldType.NUMBER).description("????????? ID"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("????????? ??????")
                        )
                ));

        // then
        then(studyService).should(times(1)).findChatRoomsById(any());
    }

    @Test
    @DisplayName("????????? ????????? ?????? API")
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
                                fieldWithPath("[].studyId").type(JsonFieldType.NUMBER).description("????????? ID"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("[].numberOfPeople").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("[].currentNumberOfPeople").type(JsonFieldType.NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("[].online").type(JsonFieldType.BOOLEAN).description("????????? ????????? ??????"),
                                fieldWithPath("[].offline").type(JsonFieldType.BOOLEAN).description("????????? ??????????????????"),
                                fieldWithPath("[].status").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("[].imageUrl").type(JsonFieldType.STRING).description("????????? ????????? URL"),
                                fieldWithPath("[].tags").type(JsonFieldType.ARRAY).description("????????? ??????"))));

        // then
        then(studyService).should(times(1)).findByUserId(any());

    }

    @Test
    @DisplayName("????????? ????????? ?????? ?????? ?????? API")
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
                                fieldWithPath("[].studyId").type(JsonFieldType.NUMBER).description("????????? ID"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("[].numberOfPeople").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("[].currentNumberOfPeople").type(JsonFieldType.NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("[].online").type(JsonFieldType.BOOLEAN).description("????????? ????????? ??????"),
                                fieldWithPath("[].offline").type(JsonFieldType.BOOLEAN).description("????????? ??????????????????"),
                                fieldWithPath("[].status").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("[].imageUrl").type(JsonFieldType.STRING).description("????????? ????????? URL"),
                                fieldWithPath("[].tags").type(JsonFieldType.ARRAY).description("????????? ??????"))));

        // then
        then(studyService).should(times(1)).findByWaitUserId(any());
    }

    @Test
    @DisplayName("????????? ID ??? ????????? ?????? API")
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
                                parameterWithName("chatRoomId").description("????????? ID")
                        ),
                        responseFields(
                                fieldWithPath("studyId").type(JsonFieldType.NUMBER).description("????????? ID"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("numberOfPeople").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("currentNumberOfPeople").type(JsonFieldType.NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("online").type(JsonFieldType.BOOLEAN).description("????????? ????????? ??????"),
                                fieldWithPath("offline").type(JsonFieldType.BOOLEAN).description("????????? ??????????????????"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("imageUrl").type(JsonFieldType.STRING).description("????????? ????????? URL")
                        )
                ));

        // then
        then(studyService).should(times(1)).findByChatRoomId(any());
    }

    @Test
    @DisplayName("????????? ?????? ?????? API")
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
                                parameterWithName("studyId").description("????????? ID"),
                                parameterWithName("chatRoomId").description("????????? ID")
                        ),
                        responseFields(
                                fieldWithPath("chatRoomId").type(JsonFieldType.NUMBER).description("????????? ID"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("????????? ??????")
                        )
                ));

        // then
        then(studyService).should(times(1)).findChatRoomByIdAndChatRoomId(any(), any());
    }

}