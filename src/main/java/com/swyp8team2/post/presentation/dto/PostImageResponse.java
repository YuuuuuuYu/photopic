package com.swyp8team2.post.presentation.dto;

public record PostImageResponse(
        Long id,
        String imageUrl,
        boolean voted
) {
}
