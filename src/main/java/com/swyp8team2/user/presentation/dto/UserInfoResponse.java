package com.swyp8team2.user.presentation.dto;

import com.swyp8team2.user.domain.User;

public record UserInfoResponse(
        Long id,
        String nickname,
        String profileUrl
) {
    public static UserInfoResponse of(User user) {
        return new UserInfoResponse(user.getId(), user.getNickname(), user.getProfileUrl());
    }
}
