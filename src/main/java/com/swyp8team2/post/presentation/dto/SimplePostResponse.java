package com.swyp8team2.post.presentation.dto;

import com.swyp8team2.common.dto.CursorDto;

import java.time.LocalDateTime;

public record SimplePostResponse(
        long id,
        String bestPickedImageUrl,
        String shareUrl,
        LocalDateTime createdAt
) implements CursorDto {

    @Override
    public long getId() {
        return id;
    }
}
