package com.swyp8team2.post.presentation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreatePostRequest(
        @NotNull
        String description,

        @Valid @NotNull
        List<VoteRequestDto> votes
) {
}
