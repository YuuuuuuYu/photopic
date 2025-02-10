package com.swyp8team2.user.presentation.dto;

public record UserInfoResponse(
        Long userId,
        String nickname,
        String profileUrl,
        String email
) {
}
