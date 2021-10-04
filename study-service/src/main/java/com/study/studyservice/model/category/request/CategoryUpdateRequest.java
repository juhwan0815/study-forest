package com.study.studyservice.model.category.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryUpdateRequest {

    @NotBlank(message = "카테고리 이름은 필수 입니다.")
    private String name;
}
