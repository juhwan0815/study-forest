package com.study.studyservice.service;

import com.study.studyservice.model.category.request.CategorySaveRequest;
import com.study.studyservice.model.category.response.CategoryResponse;

public interface CategoryService {

    CategoryResponse save(CategorySaveRequest request);
}
