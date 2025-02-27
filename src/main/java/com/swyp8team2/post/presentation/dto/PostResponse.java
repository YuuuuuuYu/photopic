package com.swyp8team2.post.presentation.dto;

import com.swyp8team2.post.domain.Post;
import com.swyp8team2.user.domain.User;

import java.time.LocalDateTime;
import java.util.List;

public record PostResponse(
        Long id,
        AuthorDto author,
        String description,
        List<PostImageResponse> images,
        String shareUrl,
        boolean isAuthor,
        LocalDateTime createdAt
) {
    public static PostResponse of(Post post, User user, List<PostImageResponse> images, boolean isAuthor) {
        return new PostResponse(
                post.getId(),
                AuthorDto.of(user),
                post.getDescription(),
                images,
                post.getShareUrl(),
                isAuthor,
                post.getCreatedAt()
        );
    }
}
