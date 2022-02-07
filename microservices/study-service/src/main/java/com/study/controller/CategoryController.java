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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/categories")
    public ResponseEntity<Map<String, Long>> create(@RequestBody @Valid CategoryCreateRequest request) {
        Long categoryId = categoryService.create(request);
        Map<String, Long> response = new HashMap<>();
        response.put("categoryId", categoryId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/categories/{categoryId}/children")
    public ResponseEntity<Map<String, Long>> createChildren(@PathVariable Long categoryId,
                                                            @RequestBody @Valid CategoryCreateRequest request) {
        Long createCategoryId = categoryService.createChildren(categoryId, request);
        Map<String, Long> response = new HashMap<>();
        response.put("categoryId", createCategoryId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/categories/{categoryId}")
    public ResponseEntity<Void> update(@PathVariable Long categoryId,
                                       @RequestBody @Valid CategoryUpdateRequest request) {
        categoryService.update(categoryId, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<Void> delete(@PathVariable Long categoryId) {
        categoryService.delete(categoryId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> findParent() {
        return ResponseEntity.ok(categoryService.findParent());
    }

    @GetMapping("/categories/{categoryId}/children")
    public ResponseEntity<List<CategoryResponse>> findByParent(@PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryService.findByParent(categoryId));
    }

}
