package com.study.notificationservice.model.user;

import com.study.notificationservice.model.tag.InterestTagResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserWithTagResponse {

    private Long id;

    private String fcmToken;

    private List<InterestTagResponse> tags;

}