package com.study.authservice;

import com.study.authservice.domain.Auth;
import com.study.authservice.model.common.CreateTokenResult;
import com.study.authservice.model.user.UserResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;

import java.util.Date;

public class AuthFixture {

    public static final  UserResponse TEST_USER_RESPONSE = new UserResponse(1L,"USER");

    public static final String TEST_ACCESS_TOKEN = Jwts.builder()
            .setSubject(String.valueOf(TEST_USER_RESPONSE.getId()))
            .claim("ROLE", TEST_USER_RESPONSE.getRole())
            .setIssuedAt(new Date())
            .setExpiration(new Date(new Date().getTime() + 86400000L))
            .signWith(SignatureAlgorithm.HS256, "study")
            .compact();

    public static final String TEST_REFRESH_TOKEN = Jwts.builder()
            .setSubject(String.valueOf(TEST_USER_RESPONSE.getId()))
            .claim("ROLE", TEST_USER_RESPONSE.getRole())
            .setIssuedAt(new Date())
            .setExpiration(new Date(new Date().getTime() + 604800000L))
            .signWith(SignatureAlgorithm.HS256, "study")
            .compact();

    public static final CreateTokenResult TEST_CREATE_TOKEN_RESULT =
            new CreateTokenResult(TEST_ACCESS_TOKEN,TEST_REFRESH_TOKEN);

    public static final Auth TEST_AUTH = new Auth(1L,1L,TEST_REFRESH_TOKEN);
}
