package com.study.authservice.model;

import lombok.Data;

@Data
public class CreateTokenResult {

    private String accessToken;

    private String refreshToken;

    public static CreateTokenResult from(String accessToken,String refreshToken){
        CreateTokenResult createTokenResult = new CreateTokenResult();
        createTokenResult.accessToken = accessToken;
        createTokenResult.refreshToken = refreshToken;
        return createTokenResult;
    }
}
