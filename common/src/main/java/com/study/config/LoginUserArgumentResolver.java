package com.study.config;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Value("${token.secret}")
    private String secretKey;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean isLoginUserAnnotation = parameter.getParameterAnnotation(LoginUser.class) != null;
        boolean isLongClass = Long.class.equals(parameter.getParameterType());
        return isLoginUserAnnotation && isLongClass;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {

        String bearerToken = webRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if(bearerToken == null){
            return null;
        }

        String jwt = bearerToken.substring(7);

        String userId = null;
        try {
            userId = Jwts.parser().setSigningKey(secretKey)
                    .parseClaimsJws(jwt)
                    .getBody().getSubject();
        } catch (Exception e){
            return null;
        }

        return Long.valueOf(userId);
    }
}
