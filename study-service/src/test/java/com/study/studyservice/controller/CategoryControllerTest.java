package com.study.studyservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.studyservice.domain.Category;
import com.study.studyservice.model.category.request.CategorySaveRequest;
import com.study.studyservice.model.category.request.CategoryUpdateRequest;
import com.study.studyservice.model.category.response.CategoryResponse;
import com.study.studyservice.service.CategoryService;
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

import java.util.ArrayList;
import java.util.List;

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
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    private final String TEST_AUTHORIZATION = "Bearer 액세스토큰";

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(WebApplicationContext wac,
               RestDocumentationContextProvider restDocumentationContextProvider) {
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
    @DisplayName("카테고리 생성 API 테스트")
    void create() throws Exception {

        CategorySaveRequest categorySaveRequest = new CategorySaveRequest();
        categorySaveRequest.setId(1L);
        categorySaveRequest.setName("백엔드");

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setId(10L);
        categoryResponse.setName("백엔드");

        given(categoryService.save(any()))
                .willReturn(categoryResponse);

        mockMvc.perform(post("/categories")
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(categorySaveRequest))
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(categoryResponse)))
                .andDo(document("category/create",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        requestFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER)
                                        .description("부모 카테고리가 존재할 경우 부모 카테고리의 ID"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("생성할 카테고리의 이름")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("생성한 카테고리의 ID"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("생성한 카테고리의 이름")
                        )
                ));

        then(categoryService).should(times(1)).save(any());
    }

    @Test
    @DisplayName("카테고리 수정 API 테스트")
    void update() throws Exception {
        CategoryUpdateRequest categoryUpdateRequest = new CategoryUpdateRequest();
        categoryUpdateRequest.setName("백엔드");

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setId(1L);
        categoryResponse.setName(categoryUpdateRequest.getName());

        given(categoryService.update(any(), any()))
                .willReturn(categoryResponse);

        mockMvc.perform(put("/categories/{categoryId}", 1)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(categoryUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(categoryResponse)))
                .andDo(document("category/update",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        pathParameters(
                                parameterWithName("categoryId").description("카테고리 ID")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("변경할 카테고리의 이름")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("변경된 카테고리의 ID"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("변경된 카테고리의 이름")
                        )
                ));

        then(categoryService).should(times(1)).update(any(), any());
    }

    @Test
    @DisplayName("카테고리 삭제 API 테스트")
    void delete() throws Exception {

        willDoNothing().given(categoryService).delete(any());

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/categories/{categoryId}", 1)
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION))
                .andExpect(status().isOk())
                .andDo(document("category/delete",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        pathParameters(
                                parameterWithName("categoryId").description("카테고리 ID")
                        )
                ));

        then(categoryService).should(times(1)).delete(any());
    }

    @Test
    @DisplayName("부모 카테고리 조회 API 테스트")
    void findParentCategory() throws Exception {
        CategoryResponse categoryResponse1 = new CategoryResponse();
        categoryResponse1.setId(1L);
        categoryResponse1.setName("개발");

        CategoryResponse categoryResponse2 = new CategoryResponse();
        categoryResponse2.setId(2L);
        categoryResponse2.setName("게임");

        List<CategoryResponse> parentList = new ArrayList<>();
        parentList.add(categoryResponse1);
        parentList.add(categoryResponse2);

        given(categoryService.findParent())
                .willReturn(parentList);

        mockMvc.perform(get("/categories/parent")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(parentList)))
                .andDo(document("category/findParent",
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("부모 카테고리 ID"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("부모 카테고리 이름")
                        )));

        then(categoryService).should(times(1)).findParent();
    }

    @Test
    @DisplayName("자식 카테고리 조회")
    void findChildCategory() throws Exception {
        CategoryResponse categoryResponse1 = new CategoryResponse();
        categoryResponse1.setId(3L);
        categoryResponse1.setName("프론트엔드");

        CategoryResponse categoryResponse2 = new CategoryResponse();
        categoryResponse2.setId(4L);
        categoryResponse2.setName("백엔드");

        List<CategoryResponse> childList = new ArrayList<>();
        childList.add(categoryResponse1);
        childList.add(categoryResponse2);

        given(categoryService.findChild(any()))
                .willReturn(childList);

        mockMvc.perform(get("/categories/{categoryId}/child", 1)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(childList)))
                .andDo(document("category/findChild",
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("자식 카테고리 ID"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("자식 카테고리 이름")
                        )));

        then(categoryService).should(times(1)).findChild(any());
    }

}