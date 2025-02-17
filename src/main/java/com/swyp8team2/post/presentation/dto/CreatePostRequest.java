package com.swyp8team2.post.presentation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreatePostRequest(
        @Size(min = 1, max = 200)
        String description,

        @Valid @NotNull @Size(min = 2)
        List<VoteRequestDto> votes
) {
}
