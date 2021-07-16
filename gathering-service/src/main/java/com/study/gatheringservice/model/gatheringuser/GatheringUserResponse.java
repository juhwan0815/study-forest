package com.study.gatheringservice.model.gatheringuser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GatheringUserResponse {

    private Long id;

    private Long userId;

    private Boolean register;
}
