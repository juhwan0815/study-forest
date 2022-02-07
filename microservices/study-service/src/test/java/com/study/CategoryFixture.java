package com.study;

import com.study.domain.Category;
import com.study.dto.category.CategoryCreateRequest;
import com.study.dto.category.CategoryResponse;
import com.study.dto.category.CategoryUpdateRequest;

import java.util.ArrayList;

public class CategoryFixture {

    public static final String TEST_CATEGORY_NAME = "개발";

    public static final Category TEST_CATEGORY = new Category(1L, TEST_CATEGORY_NAME, null, new ArrayList<>());

    public static final Category TEST_CHILD_CATEGORY = new Category(1L, TEST_CATEGORY_NAME, TEST_CATEGORY, new ArrayList<>());

    public static final CategoryCreateRequest TEST_CATEGORY_CREATE_REQUEST = new CategoryCreateRequest(TEST_CATEGORY_NAME);

    public static final CategoryUpdateRequest TEST_CATEGORY_UPDATE_REQUEST = new CategoryUpdateRequest(TEST_CATEGORY_NAME);

    public static final CategoryResponse TEST_CATEGORY_RESPONSE = new CategoryResponse(TEST_CATEGORY.getId(), TEST_CATEGORY.getName());
}
