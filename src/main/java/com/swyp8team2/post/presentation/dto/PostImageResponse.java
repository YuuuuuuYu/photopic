package com.swyp8team2.post.presentation.dto;

public record PostImageResponse(
        Long id,
        String imageName,
        String imageUrl,
        String thumbnailUrl,
        Long voteId
) {
}
