package com.study.dto.tag;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagCreateRequest {

    @NotBlank(message = "태그 내용은 필수입니다.")
    private String content;
}
