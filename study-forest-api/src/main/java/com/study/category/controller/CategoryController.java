package com.study.category.controller;

import com.study.category.dto.CategoryCreateRequest;
import com.study.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
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
}
