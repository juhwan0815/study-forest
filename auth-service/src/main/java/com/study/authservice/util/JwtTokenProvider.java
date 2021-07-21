package com.study.authservice.util;

import com.study.authservice.domain.Auth;
import com.study.authservice.exception.AuthException;
import com.study.authservice.model.common.CreateTokenResult;
import io.jsonwebtoken.Claims;
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

    public CreateTokenResult createToken(Long userId,String role,String nickName) {
        return CreateTokenResult.from(createAccessToken(userId,role,nickName), createRefreshToken(userId,role,nickName));
    }

    public String createAccessToken(Long userId,String role,String nickName) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("ROLE", role)
                .claim("nickName",nickName)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + Long.valueOf(accessTokenExpirationTime)))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String createRefreshToken(Long userId,String role,String nickName) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("ROLE", role)
                .claim("nickName",nickName)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + Long.valueOf(refreshTokenExpirationTime)))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Long getUserId(String token) {
        String jwt = token.substring(7);
        String userId = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt)
                .getBody().getSubject();
        return Long.valueOf(userId);
    }

    public String refresh(String requestRefreshToken, Auth auth){
        if (!requestRefreshToken.equals(auth.getRefreshToken())){
            throw new AuthException("서버의 refreshToken과 일치하지 않습니다.");
        }

        Claims body = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(requestRefreshToken)
                .getBody();
        String role = body.get("ROLE", String.class);
        String nickName = body.get("nickName",String.class);

        return createAccessToken(auth.getUserId(),role,nickName);
    }

}
