package com.study.studyservice.controller;

import com.study.studyservice.domain.Category;
import com.study.studyservice.model.category.request.CategorySaveRequest;
import com.study.studyservice.model.category.request.CategoryUpdateRequest;
import com.study.studyservice.model.category.response.CategoryResponse;
import com.study.studyservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/categories/{categoryId}")
    public ResponseEntity<CategoryResponse> update(@PathVariable Long categoryId,
                                                   @RequestBody @Valid CategoryUpdateRequest request){
        return ResponseEntity.ok(categoryService.update(categoryId,request));
    }

    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<Void> delete(@PathVariable Long categoryId){
        categoryService.delete(categoryId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
