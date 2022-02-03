package com.study.category.controller;

import com.study.category.dto.CategoryCreateRequest;
import com.study.category.dto.CategoryResponse;
import com.study.category.dto.CategoryUpdateRequest;
import com.study.category.service.CategoryService;
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

    @PostMapping("/api/categories/parents")
    public ResponseEntity<Map<String, Long>> createParent(@RequestBody @Valid CategoryCreateRequest request) {
        Long categoryId = categoryService.createParent(request);

        Map<String, Long> response = new HashMap<>();
        response.put("categoryId", categoryId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/categories/{categoryId}/children")
    public ResponseEntity<Map<String, Long>> createChild(@PathVariable Long categoryId,
                                                         @RequestBody @Valid CategoryCreateRequest request) {
        Long createCategoryId = categoryService.createChildren(categoryId, request);

        Map<String, Long> response = new HashMap<>();
        response.put("categoryId", createCategoryId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/api/categories/{categoryId}")
    public ResponseEntity<Void> update(@PathVariable Long categoryId,
                                       @RequestBody @Valid CategoryUpdateRequest request) {
        categoryService.update(categoryId, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/api/categories/{categoryId}")
    public ResponseEntity<Void> delete(@PathVariable Long categoryId) {
        categoryService.delete(categoryId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/api/categories/parents")
    public ResponseEntity<List<CategoryResponse>> findParents() {
        return ResponseEntity.ok(categoryService.findParents());
    }

    @GetMapping("/api/categories/{categoryId}/children")
    public ResponseEntity<List<CategoryResponse>> findChildrenById(@PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryService.findChildrenById(categoryId));
    }

}
