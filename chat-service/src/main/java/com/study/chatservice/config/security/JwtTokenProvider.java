package com.study.chatservice.config.security;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${token.secret}")
    private String secretKey;

    /**
     * Jwt Token을 복호화 하여 회원ID을 얻는다.
     */
    public Long getUserIdFromJwt(String jwt){
        String userId = getClaims(jwt).getBody().getSubject();
        return Long.valueOf(userId);
    }


    /**
     * Jwt Token을 복호화 하여 이름을 얻는다.
     */
    public String getNicknameFromJwt(String jwt){
        return getClaims(jwt).getBody().get("nickName",String.class);
    }

    /**
     * Jwt Token의 유효성을 체크한다.
     */
    public boolean validateToken(String jwt){
        return this.getClaims(jwt) != null;
    }

    private Jws<Claims> getClaims(String jwt){
        try{
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt);
        }catch (SignatureException e){
            log.error("Invalid JWT signature");
            throw e;
        }catch (MalformedJwtException e){
            log.error("Invalid JWT token");
            throw e;
        }catch (ExpiredJwtException e){
            log.error("Expired JWT token");
            throw e;
        }catch (UnsupportedJwtException e){
            log.error("Unsupported JWT token");
            throw e;
        }catch (IllegalArgumentException e){
            log.error("JWT claims String is empty");
            throw e;
        }
    }

}
