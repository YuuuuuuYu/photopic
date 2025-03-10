package com.swyp8team2.post.application;

import com.swyp8team2.common.dto.CursorBasePaginatedResponse;
import com.swyp8team2.post.presentation.dto.CreatePostRequest;
import com.swyp8team2.post.presentation.dto.CreatePostResponse;
import com.swyp8team2.post.presentation.dto.PostResponse;
import com.swyp8team2.post.presentation.dto.SimplePostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostCommandService postCommandService;
    private final PostQueryService postQueryService;

    @Transactional
    public CreatePostResponse create(Long userId, CreatePostRequest request) {
        return postCommandService.create(userId, request);
    }

    @Transactional
    public void delete(Long userId, Long postId) {
        postCommandService.delete(userId, postId);
    }

    @Transactional
    public void close(Long userId, Long postId) {
        postCommandService.close(userId, postId);
    }

    @Transactional
    public void toggleScope(Long userId, Long postId) {
        postCommandService.toggleScope(userId, postId);
    }

    public PostResponse findById(Long userId, Long postId) {
        return postQueryService.findById(userId, postId);
    }

    public CursorBasePaginatedResponse<SimplePostResponse> findUserPosts(Long userId, Long cursor, int size) {
        return postQueryService.findUserPosts(userId, cursor, size);
    }

    public CursorBasePaginatedResponse<SimplePostResponse> findVotedPosts(Long userId, Long cursor, int size) {
        return postQueryService.findVotedPosts(userId, cursor, size);
    }

    public PostResponse findByShareUrl(Long userId, String shareUrl) {
        return postQueryService.findByShareUrl(userId, shareUrl);
    }
}
