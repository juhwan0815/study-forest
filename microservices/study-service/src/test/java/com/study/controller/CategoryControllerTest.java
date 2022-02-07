package com.study.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.dto.category.CategoryResponse;
import com.study.service.CategoryService;
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

import static com.study.CategoryFixture.*;
import static com.study.CommonFixture.TEST_AUTHORIZATION;
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
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @MockBean
    private CategoryService categoryService;

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
    @DisplayName("카테고리 생성 API")
    void create() throws Exception {
        // given
        Map<String, Long> response = new HashMap<>();
        response.put("categoryId", TEST_CATEGORY.getId());

        given(categoryService.create(any()))
                .willReturn(TEST_CATEGORY.getId());

        // when
        mockMvc.perform(post("/categories")
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(TEST_CATEGORY_CREATE_REQUEST)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(document("category/create",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("카테고리 이름")
                        ),
                        responseFields(
                                fieldWithPath("categoryId").type(JsonFieldType.NUMBER).description("카테고리 ID")
                        )));

        // then
        then(categoryService).should(times(1)).create(any());
    }

    @Test
    @DisplayName("하위 카테고리 생성 API")
    void createChildren() throws Exception {
        // given
        Map<String, Long> response = new HashMap<>();
        response.put("categoryId", TEST_CATEGORY.getId());

        given(categoryService.createChildren(any(), any()))
                .willReturn(TEST_CATEGORY.getId());

        // when
        mockMvc.perform(post("/categories/{categoryId}/children", TEST_CATEGORY.getId())
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(TEST_CATEGORY_CREATE_REQUEST)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(document("category/createChildren",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("categoryId").description("카테고리 ID")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("카테고리 이름")
                        ),
                        responseFields(
                                fieldWithPath("categoryId").type(JsonFieldType.NUMBER).description("카테고리 ID")
                        )
                ));

        // then
        then(categoryService).should(times(1)).createChildren(any(), any());
    }

    @Test
    @DisplayName("카테고리 수정 API")
    void update() throws Exception {
        // given
        willDoNothing()
                .given(categoryService)
                .update(any(), any());

        // when
        mockMvc.perform(put("/categories/{categoryId}", TEST_CATEGORY.getId())
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(TEST_CATEGORY_CREATE_REQUEST)))
                .andExpect(status().isOk())
                .andDo(document("category/update",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("categoryId").description("카테고리 ID")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("카테고리 이름")
                        )
                ));

        // then
        then(categoryService).should(times(1)).update(any(), any());
    }

    @Test
    @DisplayName("카테고리 삭제 API")
    void delete() throws Exception {
        // given
        willDoNothing()
                .given(categoryService)
                .delete(any());

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/categories/{categoryId}", TEST_CATEGORY.getId())
                        .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION))
                .andExpect(status().isOk())
                .andDo(document("category/delete",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("categoryId").description("카테고리 ID")
                        )
                ));

        // then
        then(categoryService).should(times(1)).delete(any());
    }

    @Test
    @DisplayName("상위 카테고리 조회 API")
    void findParentCategory() throws Exception {
        // given
        List<CategoryResponse> result = Arrays.asList(TEST_CATEGORY_RESPONSE);

        given(categoryService.findParent())
                .willReturn(result);

        // when
        mockMvc.perform(get("/categories")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)))
                .andDo(document("category/findParent",
                        responseFields(
                                fieldWithPath("[].categoryId").type(JsonFieldType.NUMBER).description("카테고리 ID"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("카테고리 이름")
                        )
                ));

        // then
        then(categoryService).should(times(1)).findParent();
    }

    @Test
    @DisplayName("하위 카테고리 조회 API")
    void findByParent() throws Exception {
        // given
        List<CategoryResponse> result = Arrays.asList(TEST_CATEGORY_RESPONSE);
        given(categoryService.findByParent(any()))
                .willReturn(result);

        // when
        mockMvc.perform(get("/categories/{categoryId}/children", TEST_CATEGORY.getId())
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)))
                .andDo(document("category/findChild",
                        pathParameters(
                                parameterWithName("categoryId").description("카테고리 ID")
                        ),
                        responseFields(
                                fieldWithPath("[].categoryId").type(JsonFieldType.NUMBER).description("카테고리 ID"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("카테고리 이름")
                        )
                ));

        then(categoryService).should(times(1)).findByParent(any());
    }
}