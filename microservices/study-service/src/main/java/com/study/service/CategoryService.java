package com.study.service;

import com.study.dto.category.CategoryCreateRequest;
import com.study.dto.category.CategoryResponse;
import com.study.dto.category.CategoryUpdateRequest;

import java.util.List;

public interface CategoryService {

    Long create(CategoryCreateRequest request);

    Long createChildren(Long categoryId, CategoryCreateRequest request);

    void update(Long categoryId, CategoryUpdateRequest request);

    void delete(Long categoryId);

    List<CategoryResponse> findParent();

    List<CategoryResponse> findByParent(Long categoryId);
}
