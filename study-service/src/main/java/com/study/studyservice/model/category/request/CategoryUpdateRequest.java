package com.study.studyservice.model.category.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CategoryUpdateRequest {

    @NotBlank
    private String name;
}
