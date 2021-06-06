package com.study.userservice.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserImageUpdateRequest {

    private MultipartFile image;

}
