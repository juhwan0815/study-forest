package com.study.fixture;

import com.study.domain.Category;
import com.study.dto.category.CategoryCreateRequest;
import com.study.dto.category.CategoryResponse;
import com.study.dto.category.CategoryUpdateRequest;

public class CategoryFixture {

    public static final CategoryCreateRequest TEST_CATEGORY_CREATE_REQUEST = new CategoryCreateRequest("개발");

    public static final CategoryUpdateRequest TEST_CATEGORY_UPDATE_REQUEST = new CategoryUpdateRequest("외국어");

    public static final Category TEST_CATEGORY = Category.createCategory("개발", null);

    public static final Category TEST_CHILD_CATEGORY = Category.createCategory("백엔드", TEST_CATEGORY);

    public static final CategoryResponse TEST_CATEGORY_RESPONSE =  new CategoryResponse(1L, "개발");
}
