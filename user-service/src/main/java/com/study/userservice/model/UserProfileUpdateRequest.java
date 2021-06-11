package com.study.userservice.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

@Data
public class UserProfileUpdateRequest {

    private MultipartFile image;

    private boolean deleteImage;

    private boolean updateImage;

    @NotBlank
    private String nickName;
}
