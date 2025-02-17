package com.swyp8team2.post.presentation.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PostResponse(
        Long id,
        AuthorDto author,
        String description,
        List<VoteResponseDto> votes,
        String shareUrl,
        LocalDateTime createdAt
) {
}
