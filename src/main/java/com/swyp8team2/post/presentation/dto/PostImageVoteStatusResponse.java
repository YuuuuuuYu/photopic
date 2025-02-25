package com.swyp8team2.post.presentation.dto;

public record PostImageVoteStatusResponse(
        Long id,
        String imageName,
        int voteCount,
        String voteRatio
) {
}
