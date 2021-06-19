package com.study.studyservice.fixture;

import com.study.studyservice.domain.Category;
import com.study.studyservice.domain.CategoryStatus;
import com.study.studyservice.model.category.request.CategorySaveRequest;
import com.study.studyservice.model.category.request.CategoryUpdateRequest;
import com.study.studyservice.model.category.response.CategoryResponse;

public class CategoryFixture {

    public static final CategorySaveRequest TEST_CATEGORY_SAVE_REQUEST1
            = new CategorySaveRequest(null,"개발");

    public static final CategorySaveRequest TEST_CATEGORY_SAVE_REQUEST2
            = new CategorySaveRequest(1L,"프론트엔드");

    public static final Category TEST_CATEGORY1
            = new Category(1L,"개발", CategoryStatus.ACTIVE,null,null);

    public static final Category TEST_CATEGORY2
            = new Category(2L,"프론트엔드", CategoryStatus.ACTIVE,TEST_CATEGORY1,null);

    public static final CategoryUpdateRequest TEST_CATEGORY_UPDATE_REQUEST
            = new CategoryUpdateRequest("개발");

    public static final CategoryUpdateRequest TEST_CATEGORY_UPDATE_REQUEST2
            = new CategoryUpdateRequest("프론트엔드");

    public static final CategoryResponse TEST_CATEGORY_RESPONSE1
            = new CategoryResponse(1L,"개발");

    public static final CategoryResponse TEST_CATEGORY_RESPONSE2
            = new CategoryResponse(2L,"프론트엔드");

    public static final CategoryResponse TEST_CATEGORY_RESPONSE3
            = new CategoryResponse(3L,"백엔드");


    public static Category createTestCategory(){
        return new Category(1L,"개발", CategoryStatus.ACTIVE,null,null);
    }

}
