package com.study.studyservice.service;

import com.study.studyservice.model.category.request.CategorySaveRequest;
import com.study.studyservice.model.category.request.CategoryUpdateRequest;
import com.study.studyservice.model.category.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse save(CategorySaveRequest request);

    CategoryResponse update(Long categoryId,CategoryUpdateRequest request);

    void delete(Long categoryId);

    List<CategoryResponse> findParent();

    List<CategoryResponse> findChild(Long categoryId);
}
