package com.study.studyservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.studyservice.config.LoginUserArgumentResolver;
import com.study.studyservice.domain.StudyStatus;
import com.study.studyservice.model.category.response.CategoryResponse;
import com.study.studyservice.model.location.response.LocationResponse;
import com.study.studyservice.model.study.request.StudyCreateRequest;
import com.study.studyservice.model.study.request.StudyUpdateRequest;
import com.study.studyservice.model.study.response.StudyResponse;
import com.study.studyservice.service.StudyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
import org.springframework.web.filter.CharacterEncodingFilter;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(StudyController.class)
class StudyControllerTest {

    private final String TEST_AUTHORIZATION = "Bearer 액세스토큰";

    @MockBean
    private StudyService studyService;

    @MockBean
    private LoginUserArgumentResolver loginUserArgumentResolver;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void init(WebApplicationContext wac,
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
    @DisplayName("스터디 생성 API 테스트")
    void create() throws Exception {

        MockMultipartFile image = new MockMultipartFile(
                "image",
                "프로필사진.png",
                "image/png",
                "<<image>>".getBytes(StandardCharsets.UTF_8));

        StudyCreateRequest studyCreateRequest = new StudyCreateRequest();
        studyCreateRequest.setName("스프링 스터디");
        studyCreateRequest.setLocationCode("1111051500");
        studyCreateRequest.setCategoryId(1L);
        studyCreateRequest.setContent("안녕하세요 스프링 스터디입니다.");
        studyCreateRequest.setOnline(true);
        studyCreateRequest.setOffline(true);
        studyCreateRequest.setTags(Arrays.asList("JPA", "스프링"));
        studyCreateRequest.setNumberOfPeople(5);

        MockMultipartFile request = new MockMultipartFile("request",
                "request",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(studyCreateRequest).getBytes(StandardCharsets.UTF_8));

        CategoryResponse parentCategory = new CategoryResponse();
        parentCategory.setId(1L);
        parentCategory.setName("개발");
        CategoryResponse childCategory = new CategoryResponse();
        childCategory.setId(2L);
        childCategory.setName("백엔드");

        LocationResponse locationResponse = new LocationResponse();
        locationResponse.setId(1L);
        locationResponse.setCity("서울특별시");
        locationResponse.setCode("1111051500");
        locationResponse.setGu("종로구");
        locationResponse.setDong("삼청동");
        locationResponse.setRi("--리");
        locationResponse.setLen(126.980996);
        locationResponse.setLet(37.590758);
        locationResponse.setCodeType("H");

        List<String> studyTagList = new ArrayList<>();
        studyTagList.add("JPA");
        studyTagList.add("스프링");

        StudyResponse studyResponse = new StudyResponse();
        studyResponse.setId(1L);
        studyResponse.setName("스프링 스터디");
        studyResponse.setContent("스프링 스터디입니다.");
        studyResponse.setLocation(locationResponse);
        studyResponse.setOffline(true);
        studyResponse.setOffline(true);
        studyResponse.setCurrentNumberOfPeople(1);
        studyResponse.setNumberOfPeople(5);
        studyResponse.setStudyThumbnailImage("스터디 썸네일 이미지 URL");
        studyResponse.setStudyImage("스터디 이미지 URL");
        studyResponse.setStatus(StudyStatus.OPEN);
        studyResponse.setParentCategory(parentCategory);
        studyResponse.setChildCategory(childCategory);
        studyResponse.setStudyTags(studyTagList);

        given(studyService.create(any(), any(), any()))
                .willReturn(studyResponse);

        given(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);

        mockMvc.perform(multipart("/studies").file(image).file(request)
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(studyResponse)))
                .andDo(document("study/create",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        requestParts(
                                partWithName("image").description("스터디 이미지 파일").optional(),
                                partWithName("request").ignored()
                        ),
                        requestPartFields("request",
                                fieldWithPath("name").type(JsonFieldType.STRING).description("스터디 이름"),
                                fieldWithPath("numberOfPeople").type(JsonFieldType.NUMBER).description("스터디 인원"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("스터디 내용"),
                                fieldWithPath("tags").type(JsonFieldType.ARRAY).description("스터디 주제(태그)"),
                                fieldWithPath("online").type(JsonFieldType.BOOLEAN).description("스터디 온라인 여부"),
                                fieldWithPath("offline").type(JsonFieldType.BOOLEAN).description("스터디 오프라인여부"),
                                fieldWithPath("locationCode").type(JsonFieldType.STRING).description("지역 코드"),
                                fieldWithPath("categoryId").type(JsonFieldType.NUMBER).description("카테고리 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("스터디 ID"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("스터디 이름"),
                                fieldWithPath("numberOfPeople").type(JsonFieldType.NUMBER).description("스터디 인원"),
                                fieldWithPath("currentNumberOfPeople").type(JsonFieldType.NUMBER).description("스터디 현재 인원"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("스터디 내용"),
                                fieldWithPath("online").type(JsonFieldType.BOOLEAN).description("스터디 온라인 여부"),
                                fieldWithPath("offline").type(JsonFieldType.BOOLEAN).description("스터디 오프라인여부"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("스터디 상태"),
                                fieldWithPath("studyImage").type(JsonFieldType.STRING).description("스터디 이미지 URL"),
                                fieldWithPath("studyThumbnailImage").type(JsonFieldType.STRING).description("스터디 썸네일 이미지 URL"),
                                fieldWithPath("location").type(JsonFieldType.OBJECT).description("지역 정보"),
                                fieldWithPath("location.id").type(JsonFieldType.NUMBER).description("지역 ID"),
                                fieldWithPath("location.code").type(JsonFieldType.STRING).description("지역 코드"),
                                fieldWithPath("location.city").type(JsonFieldType.STRING).description("지역 시/도"),
                                fieldWithPath("location.gu").type(JsonFieldType.STRING).description("지역 시/군/구"),
                                fieldWithPath("location.dong").type(JsonFieldType.STRING).description("지역 읍/면/동"),
                                fieldWithPath("location.ri").type(JsonFieldType.STRING).description("지역 --리"),
                                fieldWithPath("location.let").type(JsonFieldType.NUMBER).description("지역 위도"),
                                fieldWithPath("location.len").type(JsonFieldType.NUMBER).description("지역 경도"),
                                fieldWithPath("location.codeType").type(JsonFieldType.STRING).description("지역 코드 타입"),
                                fieldWithPath("parentCategory").type(JsonFieldType.OBJECT).description("부모 카테고리"),
                                fieldWithPath("parentCategory.id").type(JsonFieldType.NUMBER).description("부모 카테고리 ID"),
                                fieldWithPath("parentCategory.name").type(JsonFieldType.STRING).description("부모 카테고리 이름"),
                                fieldWithPath("childCategory").type(JsonFieldType.OBJECT).description("자식 카테고리"),
                                fieldWithPath("childCategory.id").type(JsonFieldType.NUMBER).description("자식 카테고리 ID"),
                                fieldWithPath("childCategory.name").type(JsonFieldType.STRING).description("자식 카테고리 이름"),
                                fieldWithPath("studyTags").type(JsonFieldType.ARRAY).description("스터디 태그")
                        )
                ));

        then(studyService).should(times(1)).create(any(), any(), any());
    }

    @Test
    @DisplayName("스터디 수정 API 테스트")
    void update() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "프로필사진.png",
                MediaType.IMAGE_PNG_VALUE,
                "<<image>>".getBytes(StandardCharsets.UTF_8));

        StudyUpdateRequest studyUpdateRequest = new StudyUpdateRequest();
        studyUpdateRequest.setName("노드 스터디");
        studyUpdateRequest.setNumberOfPeople(10);
        studyUpdateRequest.setDeleteImage(false);
        studyUpdateRequest.setOnline(true);
        studyUpdateRequest.setOffline(true);
        studyUpdateRequest.setClose(false);
        studyUpdateRequest.setContent("노드 스터디 입니다.");
        studyUpdateRequest.setTags(Arrays.asList("노드", "자바스크립트"));
        studyUpdateRequest.setLocationCode("1111051500");
        studyUpdateRequest.setCategoryId(3L);

        MockMultipartFile request = new MockMultipartFile("request",
                "request",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(studyUpdateRequest).getBytes(StandardCharsets.UTF_8));

        CategoryResponse parentCategory = new CategoryResponse();
        parentCategory.setId(1L);
        parentCategory.setName("개발");
        CategoryResponse childCategory = new CategoryResponse();
        childCategory.setId(3L);
        childCategory.setName("백엔드");

        LocationResponse locationResponse = new LocationResponse();
        locationResponse.setId(1L);
        locationResponse.setCity("서울특별시");
        locationResponse.setCode("1111051500");
        locationResponse.setGu("종로구");
        locationResponse.setDong("삼청동");
        locationResponse.setRi("--리");
        locationResponse.setLen(126.980996);
        locationResponse.setLet(37.590758);
        locationResponse.setCodeType("H");

        StudyResponse studyResponse = new StudyResponse();
        studyResponse.setId(1L);
        studyResponse.setName(studyUpdateRequest.getName());
        studyResponse.setContent(studyUpdateRequest.getContent());
        studyResponse.setLocation(locationResponse);
        studyResponse.setOffline(true);
        studyResponse.setOffline(true);
        studyResponse.setCurrentNumberOfPeople(1);
        studyResponse.setNumberOfPeople(studyUpdateRequest.getNumberOfPeople());
        studyResponse.setStudyThumbnailImage("변경된 스터디 썸네일 이미지 URL");
        studyResponse.setStudyImage("변경된 스터디 이미지 URL");
        studyResponse.setStatus(StudyStatus.OPEN);
        studyResponse.setParentCategory(parentCategory);
        studyResponse.setChildCategory(childCategory);
        studyResponse.setStudyTags(studyUpdateRequest.getTags());

        MockMultipartHttpServletRequestBuilder builder =
                RestDocumentationRequestBuilders.fileUpload("/studies/{studyId}", 1);
        builder.with(request1 -> {
            request1.setMethod(HttpMethod.PATCH.name());
            return request1;
        });

        given(studyService.update(any(),any(),any(),any()))
                .willReturn(studyResponse);

        given(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);

        mockMvc.perform(builder.file(image).file(request)
                .header(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(studyResponse)))
                .andDo(document("study/update",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        requestParts(
                                partWithName("image").description("변경할 이미지 파일").optional(),
                                partWithName("request").ignored()
                        ),
                        requestPartFields("request",
                                fieldWithPath("name").type(JsonFieldType.STRING).description("변경할 스터디 이름"),
                                fieldWithPath("numberOfPeople").type(JsonFieldType.NUMBER).description("변경할 스터디 인원 수"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("변경할 스터디 내용"),
                                fieldWithPath("categoryId").type(JsonFieldType.NUMBER).description("변경할 스터디 카테고리 ID"),
                                fieldWithPath("tags").type(JsonFieldType.ARRAY).description("변경할 스터디 태그"),
                                fieldWithPath("deleteImage").type(JsonFieldType.BOOLEAN).description("스터디 이미지 삭제 여부"),
                                fieldWithPath("close").type(JsonFieldType.BOOLEAN).description("스터디 비공개 여부"),
                                fieldWithPath("online").type(JsonFieldType.BOOLEAN).description("스터디 온라인 여부"),
                                fieldWithPath("offline").type(JsonFieldType.BOOLEAN).description("스터디 오프라인 여부"),
                                fieldWithPath("locationCode").type(JsonFieldType.STRING).description("스터디 지역 코드")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("스터디 ID"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("변경된 스터디 이름"),
                                fieldWithPath("numberOfPeople").type(JsonFieldType.NUMBER).description("변경된 스터디 인원"),
                                fieldWithPath("currentNumberOfPeople").type(JsonFieldType.NUMBER).description("스터디 현재 인원"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("변경된 스터디 내용"),
                                fieldWithPath("online").type(JsonFieldType.BOOLEAN).description("스터디 온라인 여부"),
                                fieldWithPath("offline").type(JsonFieldType.BOOLEAN).description("스터디 오프라인여부"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("스터디 상태"),
                                fieldWithPath("studyImage").type(JsonFieldType.STRING).description("스터디 이미지 URL"),
                                fieldWithPath("studyThumbnailImage").type(JsonFieldType.STRING).description("스터디 썸네일 이미지 URL"),
                                fieldWithPath("location").type(JsonFieldType.OBJECT).description("지역 정보"),
                                fieldWithPath("location.id").type(JsonFieldType.NUMBER).description("지역 ID"),
                                fieldWithPath("location.code").type(JsonFieldType.STRING).description("지역 코드"),
                                fieldWithPath("location.city").type(JsonFieldType.STRING).description("지역 시/도"),
                                fieldWithPath("location.gu").type(JsonFieldType.STRING).description("지역 시/군/구"),
                                fieldWithPath("location.dong").type(JsonFieldType.STRING).description("지역 읍/면/동"),
                                fieldWithPath("location.ri").type(JsonFieldType.STRING).description("지역 --리"),
                                fieldWithPath("location.let").type(JsonFieldType.NUMBER).description("지역 위도"),
                                fieldWithPath("location.len").type(JsonFieldType.NUMBER).description("지역 경도"),
                                fieldWithPath("location.codeType").type(JsonFieldType.STRING).description("지역 코드 타입"),
                                fieldWithPath("parentCategory").type(JsonFieldType.OBJECT).description("부모 카테고리"),
                                fieldWithPath("parentCategory.id").type(JsonFieldType.NUMBER).description("부모 카테고리 ID"),
                                fieldWithPath("parentCategory.name").type(JsonFieldType.STRING).description("부모 카테고리 이름"),
                                fieldWithPath("childCategory").type(JsonFieldType.OBJECT).description("자식 카테고리"),
                                fieldWithPath("childCategory.id").type(JsonFieldType.NUMBER).description("자식 카테고리 ID"),
                                fieldWithPath("childCategory.name").type(JsonFieldType.STRING).description("자식 카테고리 이름"),
                                fieldWithPath("studyTags").type(JsonFieldType.ARRAY).description("스터디 태그")
                        )
                ));

        then(studyService).should(times(1)).update(any(),any(),any(),any());
    }


}