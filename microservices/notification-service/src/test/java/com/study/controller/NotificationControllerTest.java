package com.study.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.NotificationFixture;
import com.study.dto.NotificationResponse;
import com.study.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
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

import static com.study.NotificationFixture.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    private final String TEST_AUTHORIZATION = "Bearer *****";

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(WebApplicationContext wac,
               RestDocumentationContextProvider restDocumentationContextProvider) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .apply(documentationConfiguration(restDocumentationContextProvider)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();
    }

    @Test
    @DisplayName("?????? ?????? ?????? API")
    void findByUserId() throws Exception {
        Slice<NotificationResponse> result = new SliceImpl<>(Arrays.asList(TEST_NOTIFICATION_RESPONSE), PageRequest.of(0,50), true);

        given(notificationService.findByUserId(any(), any()))
                .willReturn(result);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/notifications")
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)))
                .andDo(document("notification",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).optional().description("AccessToken")
                        ),
                        requestParameters(
                                parameterWithName("page").description("????????? ??????"),
                                parameterWithName("size").description("????????? ?????????")
                        ),
                        responseFields(
                                fieldWithPath("content").type(JsonFieldType.ARRAY).description("?????? ?????? ??????"),
                                fieldWithPath("content.[].notificationId").type(JsonFieldType.NUMBER).description("?????? ID"),
                                fieldWithPath("content.[].userId").type(JsonFieldType.NUMBER).description("?????? ?????? ID"),
                                fieldWithPath("content.[].title").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("content.[].content").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("content.[].createdAt").type(JsonFieldType.STRING).description("?????? ?????? ??????"),
                                fieldWithPath("pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("?????? ?????? "),
                                fieldWithPath("pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("????????? ??????"),
                                fieldWithPath("pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("?????? ???????????? ??????"),
                                fieldWithPath("pageable.offset").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("pageable.pageNumber").type(JsonFieldType.NUMBER).description("?????? ????????? ??????"),
                                fieldWithPath("pageable.pageSize").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("pageable.paged").type(JsonFieldType.BOOLEAN).description("????????? ??????"),
                                fieldWithPath("pageable.unpaged").type(JsonFieldType.BOOLEAN).description("???????????? ??????"),
                                fieldWithPath("last").type(JsonFieldType.BOOLEAN).description("????????? ????????? ??????"),
                                fieldWithPath("size").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("number").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("sort.sorted").type(JsonFieldType.BOOLEAN).description("????????????"),
                                fieldWithPath("sort.unsorted").type(JsonFieldType.BOOLEAN).description("???????????????"),
                                fieldWithPath("sort.empty").type(JsonFieldType.BOOLEAN).description("?????? ???????????? ??????"),
                                fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER).description("?????? ????????? ??????"),
                                fieldWithPath("first").type(JsonFieldType.BOOLEAN).description("?????? ????????? ??????"),
                                fieldWithPath("empty").type(JsonFieldType.BOOLEAN).description("?????? ???????????? ??????")
                        )));

        then(notificationService).should(times(1)).findByUserId(any(), any());
    }
}