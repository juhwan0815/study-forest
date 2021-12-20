package com.study.filter;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Date;

@Component
public class AdminFilter extends AbstractGatewayFilterFactory<AdminFilter.Config> {

    private final Environment env;

    public AdminFilter(Environment env) {
        super(Config.class);
        this.env = env;
    }

    public static class Config {
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No Authorization Header", HttpStatus.UNAUTHORIZED);
            }

            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String jwt = authorizationHeader.replace("Bearer ", "");

            ValidResult result = isJwtValid(jwt);
            if (!result.isValid()) {
                return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
            }

            if (!result.isAdmin()) {
                return onError(exchange, "JWT token is not admin", HttpStatus.FORBIDDEN);
            }

            return chain.filter(exchange);
        });
    }

    private ValidResult isJwtValid(String jwt) {
        ValidResult validResult = new ValidResult(true, true);

        String subject = null;
        String role = null;
        try {
            Claims body = Jwts.parser().setSigningKey(env.getProperty("token.secret"))
                    .parseClaimsJws(jwt).getBody();
            subject = body.getSubject();
            role = body.get("ROLE", String.class);
            if (body.getExpiration().before(new Date())) {
                validResult.changeNotValid();
            }
        } catch (Exception ex) {
            validResult.changeNotValid();
        }

        if (subject == null || subject.isEmpty()) {
            validResult.changeNotValid();
        }

        if (role == null || role.isEmpty() || !role.equals("ADMIN")) {
            validResult.changeNotAdmin();
        }
        return validResult;
    }

    private Mono<Void> onError(ServerWebExchange exchange,
                               String error,
                               HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    @Getter
    @AllArgsConstructor
    static class ValidResult {

        private boolean valid;

        private boolean admin;

        public void changeNotValid() {
            this.valid = false;
        }

        public void changeNotAdmin() {
            this.admin = false;
        }
    }
}
