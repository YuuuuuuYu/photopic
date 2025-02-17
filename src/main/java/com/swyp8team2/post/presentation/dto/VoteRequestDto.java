package com.swyp8team2.post.presentation.dto;

import jakarta.validation.constraints.NotEmpty;

public record VoteRequestDto(
        @NotEmpty
        String imageUrl
) {
}
