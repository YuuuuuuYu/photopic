package com.swyp8team2.post.presentation.dto;

import jakarta.validation.constraints.NotNull;

public record UpdatePostRequest(
        @NotNull
        String description
) {
}
