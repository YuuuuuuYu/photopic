package com.swyp8team2.user.presentation.dto;

import com.swyp8team2.user.domain.Role;
import com.swyp8team2.user.domain.User;

public record UserMyInfoResponse(
        Long id,
        String nickname,
        String profileImageUrl,
        Role role
) {
    public static UserMyInfoResponse of(User user) {
        return new UserMyInfoResponse(user.getId(), user.getNickname(), user.getProfileUrl(), user.getRole());
    }
}
