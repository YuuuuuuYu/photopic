package com.swyp8team2.vote.presentation.dto;

public record PostImageVoteStatusResponse(
        Long id,
        String imageName,
        int voteCount,
        String voteRatio
) {
}
