package com.swyp8team2.post.presentation.dto;

public record VoteResponseDto(
        Long id,
        String imageUrl,
        int voteCount,
        String voteRatio,
        boolean voted
) {
}
