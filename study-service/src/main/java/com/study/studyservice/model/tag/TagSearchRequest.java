package com.study.studyservice.model.tag;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagSearchRequest {

    @NotBlank(message = "검색어는 필수입니다.")
    private String name;

}
