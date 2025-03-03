package com.swyp8team2.auth.presentation.dto;

import com.swyp8team2.auth.application.jwt.TokenPair;
import com.swyp8team2.user.domain.Role;

public record TokenResponse(
        TokenPair tokenPair,
        Long userId,
        Role role
) { }
