package com.study.chatservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.chatservice.ChatRoomFixture;
import com.study.chatservice.config.LoginUserArgumentResolver;
import com.study.chatservice.domain.ChatMessage;
import com.study.chatservice.model.chatroom.ChatRoomResponse;
import com.study.chatservice.service.ChatRoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.study.chatservice.ChatRoomFixture.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(ChatRoomController.class)
class ChatRoomControllerTest {

    private final String TEST_AUTHORIZATION = "Bearer 액세스토큰";

    @MockBean
    private ChatRoomService chatRoomService;

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
    @DisplayName("채팅방 생성 API 테스트")
    void create() throws Exception {
        // given
        given(chatRoomService.create(any(), any()))
                .willReturn(TEST_CHAT_ROOM_RESPONSE);

        // when
        mockMvc.perform(post("/studies/{studyId}/chatRooms", 1)
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(TEST_CHAT_ROOM_CREATE_REQUEST))
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_CHAT_ROOM_RESPONSE)))
                .andDo(document("chatRoom/create",
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("studyId").description("스터디 ID")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("채팅방 이름")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("채팅방 ID"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("채팅방 이름"),
                                fieldWithPath("studyId").type(JsonFieldType.NUMBER).description("채팅방이 속한 스터디 ID")
                        )));

        // then
        then(chatRoomService).should(times(1)).create(any(), any());
    }

    @Test
    @DisplayName("채팅방 수정 API 테스트")
    void update() throws Exception {
        // given
        given(chatRoomService.update(any(), any()))
                .willReturn(TEST_CHAT_ROOM_RESPONSE);

        // when
        mockMvc.perform(patch("/chatRooms/{chatRoomId}", 1)
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(TEST_CHAT_ROOM_UPDATE_REQUEST))
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TEST_CHAT_ROOM_RESPONSE)))
                .andDo(document("chatRoom/update",
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("chatRoomId").description("채팅방 ID")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("변경할 채팅방 이름")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("채팅방 ID"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("변경된 채팅방 이름"),
                                fieldWithPath("studyId").type(JsonFieldType.NUMBER).description("채팅방이 속한 스터디 ID")
                        )));

        // then
        then(chatRoomService).should(times(1)).update(any(), any());
    }

    @Test
    @DisplayName("채팅방 삭제 API 테스트")
    void delete() throws Exception {
        // given
        willDoNothing()
                .given(chatRoomService)
                .delete(any());

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/chatRooms/{chatRoomId}", 1)
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(document("chatRoom/delete",
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("chatRoomId").description("채팅방 ID")
                        )));

        // then
        then(chatRoomService).should(times(1)).delete(any());
    }


    @Test
    @DisplayName("채팅방 조회 API 테스트")
    void findByStudyId() throws Exception {
        List<ChatRoomResponse> result = Arrays.asList(TEST_CHAT_ROOM_RESPONSE, TEST_CHAT_ROOM_RESPONSE2);
        // given
        given(chatRoomService.findByStudyId(any()))
                .willReturn(result);

        // when
        mockMvc.perform(get("/studies/{studyId}/chatRooms", 1)
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)))
                .andDo(document("chatRoom/findByStudyId",
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("studyId").description("스터디 ID")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("채팅방 ID"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("채팅방 이름"),
                                fieldWithPath("[].studyId").type(JsonFieldType.NUMBER).description("채팅방이 속한 스터디 ID")
                        )));

        // then
        then(chatRoomService).should(times(1)).findByStudyId(any());
    }


}


