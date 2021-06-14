package com.study.userservice.kafka.message;

import lombok.Data;

@Data
public class StudyJoinMessage {

    private Long userId;

    private Long studyId;

    private boolean create;

    private boolean fail;

    private boolean success;
}
