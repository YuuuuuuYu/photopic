package com.swyp8team2.post.presentation.dto;

import com.swyp8team2.common.dto.CursorDto;
import com.swyp8team2.post.domain.Post;

import java.time.LocalDateTime;

public record SimplePostResponse(
        long id,
        String bestPickedImageUrl,
        String shareUrl,
        LocalDateTime createdAt
) implements CursorDto {

    public static SimplePostResponse of(Post post, String bestPickedImageUrl) {
        return new SimplePostResponse(
                post.getId(),
                bestPickedImageUrl,
                post.getShareUrl(),
                post.getCreatedAt()
        );
    }

    @Override
    public long getId() {
        return id;
    }
}
