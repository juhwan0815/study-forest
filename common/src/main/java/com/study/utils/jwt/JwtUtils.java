package com.study.utils.jwt;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    @Value("${token.secret}")
    private String secretKey;

    public String createToken(Long userId, String role, String nickName, String expirationTime) {
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

    public String createToken(String refreshToken, String expirationTime) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(String.valueOf(getUserId(refreshToken)))
                .claim("ROLE", getRole(refreshToken))
                .claim("nickName", getNickName(refreshToken))
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

    public boolean validate(String jwt){
        return this.getClaims(jwt) != null;
    }

    private Jws<Claims> getClaims(String jwt) {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt);
        } catch (SignatureException e) {
            throw e;
        } catch (MalformedJwtException e) {
            throw e;
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (UnsupportedJwtException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }
}

