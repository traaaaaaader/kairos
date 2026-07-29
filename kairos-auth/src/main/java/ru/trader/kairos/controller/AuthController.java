package ru.trader.kairos.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.trader.kairos.dto.AccessTokenResponse;
import ru.trader.kairos.dto.LoginRequest;
import ru.trader.kairos.dto.RegisterRequest;
import ru.trader.kairos.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.trader.kairos.security.CookieService;
import ru.trader.kairos.security.JwtService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final CookieService cookieService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<AccessTokenResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response) {

        UserService.AuthResult result = userService.register(request);
        cookieService.setRefreshTokenCookie(response, result.refreshToken());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AccessTokenResponse(result.accessToken()));
    }

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {

        UserService.AuthResult result = userService.login(request);
        cookieService.setRefreshTokenCookie(response, result.refreshToken());
        return ResponseEntity.ok(new AccessTokenResponse(result.accessToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> refresh(
            HttpServletRequest request,
            HttpServletResponse response) {

        String refreshToken = cookieService.getRefreshTokenFromCookie(request)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token not found"));

        UserService.AuthResult result = userService.refresh(refreshToken);
        cookieService.setRefreshTokenCookie(response, result.refreshToken());
        return ResponseEntity.ok(new AccessTokenResponse(result.accessToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletResponse response) {

        cookieService.clearRefreshTokenCookie(response);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/validate")
    public ResponseEntity<Void> validate(
            HttpServletRequest request,
            HttpServletResponse response) {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authHeader.substring(7);

        if (!jwtService.isTokenValid(token) || !jwtService.isAccessToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = jwtService.extractUserId(token);
        String email = jwtService.extractEmail(token);

        response.setHeader("X-User-Id", userId.toString());
        response.setHeader("X-User-Email", email);

        return ResponseEntity.ok().build();
    }
}
