package com.study.utils.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    @Value("${token.secret}")
    private String secretKey;

    public String createToken(Long userId, String role, String nickName, Long expirationTime) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("ROLE", role)
                .claim("nickName", nickName)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + Long.valueOf(expirationTime)))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Long getUserId(String token) {
        String jwt = token.substring(7);
        String userId = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt)
                .getBody().getSubject();
        return Long.valueOf(userId);
    }

    public String getRole(String token){
        Claims body = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                .getBody();
        return body.get("ROLE", String.class);
    }

    public String getNickName(String token){
        Claims body = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                .getBody();
        return body.get("nickName", String.class);
    }

}
