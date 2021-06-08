package com.study.studyservice.model.category.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CategorySaveRequest {

    private Long id;

    @NotBlank
    private String name;
}
