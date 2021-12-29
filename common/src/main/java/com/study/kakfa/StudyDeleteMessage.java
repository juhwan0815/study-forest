package com.study.kakfa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyDeleteMessage {

    private Long studyId;

    private List<Long> chatRoomIds;

    public static StudyDeleteMessage from(Long studyId, List<Long> chatRoomIds){
        return new StudyDeleteMessage(studyId, chatRoomIds);
    }
}
