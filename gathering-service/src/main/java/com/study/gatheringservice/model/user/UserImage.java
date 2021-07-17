package com.study.gatheringservice.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserImage {

    private String thumbnailImage;

    private String profileImage;
}
