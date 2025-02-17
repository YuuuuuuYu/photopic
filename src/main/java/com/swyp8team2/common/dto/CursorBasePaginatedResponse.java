package com.swyp8team2.common.dto;

import org.springframework.data.domain.Slice;

import java.util.List;

public record CursorBasePaginatedResponse<T>(
        Long nextCursor,
        boolean hasNext,
        List<T> data
) {

    public static <T extends CursorDto> CursorBasePaginatedResponse<T> of(Slice<T> slice) {
        return new CursorBasePaginatedResponse<>(
                slice.getContent().isEmpty() ? null : slice.getContent().getLast().getId(),
                slice.hasNext(),
                slice.getContent()
        );
    }
}
