package com.swyp8team2.post.presentation.dto;

import com.swyp8team2.common.dto.CursorDto;
import com.swyp8team2.post.domain.Status;

import java.util.List;

public record FeedResponse(
        Long id,
        AuthorDto author,
        List<PostImageResponse> images,
        Status status,
        String description,
        String shareUrl,
        boolean isAuthor,
        Long participantCount,
        Long commentCount

) implements CursorDto {

    public static FeedResponse of(FeedDto feedDto, AuthorDto author, List<PostImageResponse> images, boolean isAuthor) {
        return new FeedResponse(
                feedDto.postId(),
                author,
                images,
                feedDto.status(),
                feedDto.description(),
                feedDto.shareUrl(),
                isAuthor,
                feedDto.participantCount(),
                feedDto.commentCount()
        );
    }

    @Override
    public long getId() {
        return id;
    }
}
