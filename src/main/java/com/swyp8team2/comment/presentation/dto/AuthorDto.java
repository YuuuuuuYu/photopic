package com.swyp8team2.comment.presentation.dto;

public record AuthorDto(
        Long userId,
        String nickname,
        String profileUrl
) {
}
