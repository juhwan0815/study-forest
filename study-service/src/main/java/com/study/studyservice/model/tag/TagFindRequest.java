package com.study.studyservice.model.tag;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagFindRequest {

    @NotNull(message = "태그 ID 리스트는 필수입니다.")
    private List<Long> tagIdList;
}
