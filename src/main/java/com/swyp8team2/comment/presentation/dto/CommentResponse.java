package com.swyp8team2.comment.presentation.dto;

import java.time.LocalDateTime;

public record CommentResponse(
        Long commentId,
        String content,
        AuthorDto author,
        Long voteId,
        LocalDateTime createdAt
) {
}
