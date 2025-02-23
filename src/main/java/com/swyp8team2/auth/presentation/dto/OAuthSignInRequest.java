package com.swyp8team2.auth.presentation.dto;

import jakarta.validation.constraints.NotNull;

public record OAuthSignInRequest(
        @NotNull
        String code
) {
}
