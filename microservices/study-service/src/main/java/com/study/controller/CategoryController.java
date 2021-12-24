package com.study.controller;

import com.study.dto.category.CategoryCreateRequest;
import com.study.dto.category.CategoryResponse;
import com.study.dto.category.CategoryUpdateRequest;
import com.study.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/categories")
    public ResponseEntity<CategoryResponse> create(@RequestBody @Valid CategoryCreateRequest request) {
        return ResponseEntity.ok(categoryService.create(request));
    }

    @PostMapping("/categories/{categoryId}/children")
    public ResponseEntity<CategoryResponse> createChildren(@PathVariable Long categoryId,
                                                           @RequestBody @Valid CategoryCreateRequest request) {
        return ResponseEntity.ok(categoryService.createChildren(categoryId, request));
    }

    @PutMapping("/categories/{categoryId}")
    public ResponseEntity<CategoryResponse> update(@PathVariable Long categoryId,
                                                   @RequestBody @Valid CategoryUpdateRequest request) {
        return ResponseEntity.ok(categoryService.update(categoryId, request));
    }

    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<Void> delete(@PathVariable Long categoryId) {
        categoryService.delete(categoryId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> findParentCategory(){
        return ResponseEntity.ok(categoryService.findParent());
    }

}
