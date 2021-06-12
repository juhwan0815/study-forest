package com.study.userservice.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserProfileUpdateRequest {

    private boolean deleteImage;

    @NotBlank
    private String nickName;
}
