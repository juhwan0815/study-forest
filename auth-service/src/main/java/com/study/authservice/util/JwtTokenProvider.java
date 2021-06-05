package com.study.authservice.util;

import com.study.authservice.exception.UserException;
import com.study.authservice.model.CreateTokenResult;
import com.study.authservice.model.UserResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${token.secret}")
    private String secretKey;

    @Value("${token.access_token.expiration_time}")
    private String accessTokenExpirationTime;

    @Value("${token.refresh_token.expiration_time}")
    private String refreshTokenExpirationTime;

    public CreateTokenResult createToken(UserResponse user){
        return CreateTokenResult.from(createAccessToken(user),createRefreshToken(user));
    }

    public String createAccessToken(UserResponse user){
        Date now = new Date();
        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim("ROLE",user.getRole())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + Long.valueOf(accessTokenExpirationTime)))
                .signWith(SignatureAlgorithm.HS256,secretKey)
                .compact();
    }

    public String createRefreshToken(UserResponse user){
        Date now = new Date();
        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim("ROLE",user.getRole())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + Long.valueOf(refreshTokenExpirationTime)))
                .signWith(SignatureAlgorithm.HS256,secretKey)
                .compact();
    }
}