package com.swyp8team2.post.presentation.dto;

import com.swyp8team2.common.dto.CursorDto;
import com.swyp8team2.post.domain.Post;
import com.swyp8team2.post.domain.Status;
import com.swyp8team2.user.domain.User;

import java.util.List;

public record FeedResponse(
        Long id,
        AuthorDto author,
        List<PostImageResponse> images,
        Status status,
        String description,
        String shareUrl,
        boolean isAuthor,
        int participantCount,
        int commentCount

) implements CursorDto {

    public static FeedResponse of(Post post,
                                  User user,
                                  List<PostImageResponse> images,
                                  int participantCount,
                                  int commentCount,
                                  boolean isAuthor) {
        return new FeedResponse(
                post.getId(),
                AuthorDto.of(user),
                images,
                post.getStatus(),
                post.getDescription(),
                post.getShareUrl(),
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
