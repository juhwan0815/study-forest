package com.study.notificationservice.model.study;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyUserResponse {

    private Long id;

    private Long userId;

    private String fcmToken;

}
