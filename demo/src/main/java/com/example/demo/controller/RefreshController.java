package com.example.demo.controller;

import com.example.demo.dto.RefreshTokenRequestDTO;
import com.example.demo.entity.RefreshToken;
import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.security.CustomUserDetailsService;
import com.example.demo.util.CustomJwtException;
import com.example.demo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class RefreshController {

    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomUserDetailsService customUserDetailsService;

    @PostMapping("/api/account/refresh")
    public Map<String, Object> refresh(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDto) {

        String clientRefreshToken = refreshTokenRequestDto.getRefreshToken();

        // If the refresh token does not exist in the request, throw an exception.
        if (clientRefreshToken == null) {
            throw new CustomJwtException("NULL_REFRESH");
        }

        // Check if the refresh token exists in the Redis store
        Optional<RefreshToken> refreshTokenOptional =
                refreshTokenRepository.findById(clientRefreshToken);

        if (refreshTokenOptional.isEmpty()) {
            throw new CustomJwtException("INVALID_REFRESH");
        }

        RefreshToken serverRefreshToken = refreshTokenOptional.get();
        String username = serverRefreshToken.getUsername();
        String password = serverRefreshToken.getPassword();

        // If the refresh token is valid, generate new access token
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("password", password);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        claims.put("roleNames", userDetails.getAuthorities());
        String newAccessToken = JwtUtil.generateToken(claims, 5);

        // Generate new refresh token if it is less than an hour until the expiration of the
        // refresh token, and save it in the Redis store.
        if (checkTime(serverRefreshToken.getExpiryDate())) {
            System.out.println("새로운 리프레시 토큰 생성한다");
            String newRefreshToken = JwtUtil.generateToken(claims, 10);
            Long newRefreshTokenExpiry = JwtUtil.getExpirationDateFromToken(newRefreshToken);
            Instant newRefreshTokenExpiryDate = Instant.ofEpochMilli(newRefreshTokenExpiry);
            DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
            String formattedExpiryDate = formatter.format(newRefreshTokenExpiryDate);
            serverRefreshToken.setRefreshToken(newRefreshToken);
            serverRefreshToken.setExpiryDate(formattedExpiryDate);
            refreshTokenRepository.save(serverRefreshToken);
            return Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken);
        }
        // If it is more than an hour until the expiration of the refresh token, return the existing refresh token.
        System.out.println("기존 리프레시 토큰 그대로 반환한다");
        return Map.of("accessToken", newAccessToken, "refreshToken", clientRefreshToken);
    }

    private boolean checkTime(String exp) {
        // JWT exp를 Instant로 파싱
        Instant expInstant = Instant.parse(exp);

        // Instant를 OffsetDateTime으로 변환
        OffsetDateTime expDateTime = expInstant.atOffset(ZoneOffset.UTC);

        // 현재 시간과의 차이 계산 - Duration 사용
        Duration duration = Duration.between(OffsetDateTime.now(ZoneOffset.UTC), expDateTime);

        // 분 단위 계산
        long leftMin = duration.toMinutes();

        // 1시간 이하로 남았으면 true 반환
        return leftMin <= 60 && !duration.isNegative();
    }


}
