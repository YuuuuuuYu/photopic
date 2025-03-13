package com.swyp8team2.post.presentation.dto;

import com.swyp8team2.post.domain.Status;

public record FeedDto(
        Long postId,
        Status status,
        String description,
        String shareUrl,
        Long postUserId,
        String nickname,
        String profileUrl,
        Long participantCount,
        Long commentCount) {
}