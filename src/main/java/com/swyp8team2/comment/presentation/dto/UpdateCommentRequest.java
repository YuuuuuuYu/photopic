package com.swyp8team2.comment.presentation.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateCommentRequest(
        @NotNull
        String content
) {
}
