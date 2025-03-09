package com.swyp8team2.comment.presentation.dto;

import jakarta.validation.constraints.NotEmpty;

public record CommentRequest(
        @NotEmpty
        String content
) {
}
