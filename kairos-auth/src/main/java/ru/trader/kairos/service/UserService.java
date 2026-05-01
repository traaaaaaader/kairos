package ru.trader.kairos.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.trader.kairos.dto.LoginRequest;
import ru.trader.kairos.dto.RegisterRequest;
import ru.trader.kairos.entity.User;
import ru.trader.kairos.reader.UserReader;
import ru.trader.kairos.repositories.UserRepository;
import ru.trader.kairos.security.JwtService;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserReader userReader;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public record AuthResult(String accessToken, String refreshToken) {}

    @Transactional
    public AuthResult register(RegisterRequest request) {

        if (userReader.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already in use");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmailConfirmed(false);

        userRepository.save(user);

        return generateTokens(user);
    }

    @Transactional(readOnly = true)
    public AuthResult login(LoginRequest request) {

        User user = userReader.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password")
                );

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        return generateTokens(user);
    }

    @Transactional(readOnly = true)
    public AuthResult refresh(String refreshToken) {

        if (!jwtService.isTokenValid(refreshToken) || !jwtService.isRefreshToken(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }
        Long userId;
        try {
            userId = jwtService.extractUserId(refreshToken);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token payload");
        }

        User user = userReader.findById(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found")
                );

        return generateTokens(user);
    }

    private AuthResult generateTokens(User user) {

        String accessToken = jwtService.generateAccessToken(
                user.getId(),
                user.getEmail()
        );

        String refreshToken = jwtService.generateRefreshToken(
                user.getId(),
                user.getEmail()
        );

        return new AuthResult(accessToken, refreshToken);
    }
}