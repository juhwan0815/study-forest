package com.study.userservice.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateProfileRequest {

    @NotNull(message = "이미지 삭제 여부는 필수입니다.")
    private Boolean deleteImage;

    @NotBlank(message = "변경 닉네임은 필수입니다.")
    private String nickName;
}
