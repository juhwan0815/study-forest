package com.study.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFindRequest {

    @NotNull(message = "회원 ID 리스트는 필수입니다.")
    private List<Long> userIds;
}
