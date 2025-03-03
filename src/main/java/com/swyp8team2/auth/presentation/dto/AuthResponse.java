package com.swyp8team2.auth.presentation.dto;

public record AuthResponse(String accessToken, Long userId, com.swyp8team2.user.domain.Role role) {
}
