package com.swyp8team2.post.presentation.dto;

import jakarta.validation.constraints.NotNull;

public record VoteRequestDto(
        @NotNull
        Long imageFileId
) {
}
