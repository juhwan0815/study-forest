package com.study.service;

import com.study.dto.category.CategoryCreateRequest;
import com.study.dto.category.CategoryResponse;

public interface CategoryService {

    CategoryResponse create(CategoryCreateRequest request);
}
