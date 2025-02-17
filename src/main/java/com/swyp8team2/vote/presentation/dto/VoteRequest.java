package com.swyp8team2.vote.presentation.dto;

import jakarta.validation.constraints.NotNull;

public record VoteRequest(
        @NotNull
        Long voteId
) {
}
