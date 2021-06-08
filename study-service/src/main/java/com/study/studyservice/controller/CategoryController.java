package com.study.studyservice.controller;

import com.study.studyservice.model.category.request.CategorySaveRequest;
import com.study.studyservice.model.category.response.CategoryResponse;
import com.study.studyservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@CrossOrigin(origins = "*",allowedHeaders = "*")
@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/categories")
    public ResponseEntity<CategoryResponse> create(@RequestBody @Valid CategorySaveRequest request){
        return ResponseEntity.ok(categoryService.save(request));
    }

}
