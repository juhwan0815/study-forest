package com.study.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateNickNameRequest {

    @NotBlank(message = "변경 닉네임은 필수입니다.")
    private String nickName;
}
