package com.swyp8team2.post.presentation.dto;

import com.swyp8team2.post.domain.Post;
import com.swyp8team2.user.domain.User;

import java.time.LocalDateTime;
import java.util.List;

public record PostResponse(
        Long id,
        AuthorDto author,
        String description,
        List<PostImageResponse> votes,
        String shareUrl,
        LocalDateTime createdAt
) {
    public static PostResponse of(Post post, User user, List<PostImageResponse> votes) {
        return new PostResponse(
                post.getId(),
                AuthorDto.of(user),
                post.getDescription(),
                votes,
                post.getShareUrl(),
                post.getCreatedAt()
        );
    }
}
