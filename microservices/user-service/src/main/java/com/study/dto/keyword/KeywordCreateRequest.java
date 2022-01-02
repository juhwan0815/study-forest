package com.study.dto.keyword;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeywordCreateRequest {

    @NotBlank(message = "키워드 내용은 필수 값입니다.")
    private String content;
}
