package com.swyp8team2.auth.presentation.dto;

import com.swyp8team2.auth.application.jwt.TokenPair;

public record TokenResponse(
        TokenPair tokenPair,
        Long userId
) {
}
