package com.swyp8team2.post.presentation.dto;

public record PostImageVoteStatusResponse(
        String imageName,
        int voteCount,
        String voteRatio
) {
}
