package com.study.userservice.model.user;

import com.study.userservice.domain.User;
import com.study.userservice.model.interestTag.InterestTagResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserWithTagResponse {

    private Long id;

    private String fcmToken;

    private List<InterestTagResponse> tags;

    public static UserWithTagResponse from(User user){
        UserWithTagResponse userWithTagResponse = new UserWithTagResponse();
        userWithTagResponse.id = user.getId();
        userWithTagResponse.fcmToken = user.getFcmToken();
        userWithTagResponse.tags = user.getInterestTags().stream()
                .map(interestTag -> InterestTagResponse.from(interestTag))
                .collect(Collectors.toList());
        return userWithTagResponse;
    }


}
