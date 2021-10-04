package com.study.studyservice.model.category.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class
CategorySaveRequest {

    private Long id;

    @NotBlank(message = "카테고리 이름은 필수입니다.")
    private String name;
}
