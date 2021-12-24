package com.study.service;

import com.study.dto.category.CategoryCreateRequest;
import com.study.dto.category.CategoryResponse;
import com.study.dto.category.CategoryUpdateRequest;

import java.util.List;

public interface CategoryService {

    CategoryResponse create(CategoryCreateRequest request);

    CategoryResponse createChildren(Long categoryId, CategoryCreateRequest request);

    CategoryResponse update(Long categoryId, CategoryUpdateRequest request);

    void delete(Long categoryId);

    List<CategoryResponse> findParent();
}
