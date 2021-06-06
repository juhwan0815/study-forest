package com.study.userservice.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserNickNameUpdateRequest {

    @NotBlank
    private String nickName;

}
