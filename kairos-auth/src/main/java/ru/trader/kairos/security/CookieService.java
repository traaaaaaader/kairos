package ru.trader.kairos.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class CookieService {

    private static final String REFRESH_COOKIE_NAME = "refresh_token";

    @Value("${jwt.refresh-token-expiration}")
    private long refreshExpiration;

    public void setRefreshTokenCookie(HttpServletResponse response, String token) {
        response.addHeader("Set-Cookie",
                String.format("%s=%s; Max-Age=%d; Path=/auth; HttpOnly; Secure; SameSite=Strict",
                        REFRESH_COOKIE_NAME,
                        token,
                        (int) (refreshExpiration / 1000))
        );
    }


    public Optional<String> getRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return Optional.empty();
        return Arrays.stream(request.getCookies())
                .filter(c -> REFRESH_COOKIE_NAME.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    public void clearRefreshTokenCookie(HttpServletResponse response) {
        response.addHeader("Set-Cookie",
                String.format("%s=; Max-Age=0; Path=/auth; HttpOnly; Secure; SameSite=Strict",
                        REFRESH_COOKIE_NAME));
    }
}