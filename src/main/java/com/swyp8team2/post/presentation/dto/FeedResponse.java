package com.swyp8team2.post.presentation.dto;

import com.swyp8team2.common.dto.CursorDto;
import com.swyp8team2.post.domain.Post;
import com.swyp8team2.post.domain.Status;

import java.util.List;

public record FeedResponse(
        Long id,
        List<PostImageResponse> images,
        Status status,
        String description,
        boolean isAuthor,
        int participantCount,
        int commentCount

) implements CursorDto {

    public static FeedResponse of(Post post,
                                  List<PostImageResponse> images,
                                  int participantCount,
                                  int commentCount,
                                  boolean isAuthor) {
        return new FeedResponse(
                post.getId(),
                images,
                post.getStatus(),
                post.getDescription(),
                isAuthor,
                participantCount,
                commentCount
        );
    }

    @Override
    public long getId() {
        return id;
    }
}
