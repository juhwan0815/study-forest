package com.study.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateAreaRequest {

    @NotBlank(message = "지역코드는 필수입니다.")
    private String code;
}
