package com.swyp8team2.post.presentation;

import com.swyp8team2.auth.domain.UserInfo;
import com.swyp8team2.common.dto.CursorBasePaginatedResponse;
import com.swyp8team2.post.application.PostService;
import com.swyp8team2.post.presentation.dto.AuthorDto;
import com.swyp8team2.post.presentation.dto.CreatePostRequest;
import com.swyp8team2.post.presentation.dto.PostResponse;
import com.swyp8team2.post.presentation.dto.SimplePostResponse;
import com.swyp8team2.post.presentation.dto.VoteResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @PostMapping("")
    public ResponseEntity<Void> createPost(
            @Valid @RequestBody CreatePostRequest request,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        postService.create(userInfo.userId(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{shareUrl}")
    public ResponseEntity<PostResponse> findPost(@PathVariable("shareUrl") String shareUrl) {
        return ResponseEntity.ok(new PostResponse(
                1L,
                new AuthorDto(
                        1L,
                        "author",
                        "https://image.photopic.site/profile-image"
                ),
                "description",
                List.of(
                        new VoteResponseDto(1L, "https://image.photopic.site/1", 3, "60.0", true),
                        new VoteResponseDto(2L, "https://image.photopic.site/2", 2, "40.0", false)
                ),
                "https://photopic.site/shareurl",
                LocalDateTime.of(2025, 2, 13, 12, 0)
        ));
    }

    @DeleteMapping("/{shareUrl}")
    public ResponseEntity<PostResponse> deletePost(
            @PathVariable("shareUrl") String shareUrl,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<CursorBasePaginatedResponse<SimplePostResponse>> findMyPosts(
            @RequestParam(name = "cursor", required = false) Long cursor,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        return ResponseEntity.ok(new CursorBasePaginatedResponse<>(
                1L,
                false,
                List.of(
                        new SimplePostResponse(
                                1L,
                                "https://image.photopic.site/1",
                                "https://photopic.site/shareurl",
                                LocalDateTime.of(2025, 2, 13, 12, 0)
                        )
                )
        ));
    }

    @GetMapping("/voted")
    public ResponseEntity<CursorBasePaginatedResponse<SimplePostResponse>> findVotedPosts(
            @RequestParam(name = "cursor", required = false) Long cursor,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        return ResponseEntity.ok(new CursorBasePaginatedResponse<>(
                1L,
                false,
                List.of(
                        new SimplePostResponse(
                                1L,
                                "https://image.photopic.site/1",
                                "https://photopic.site/shareurl",
                                LocalDateTime.of(2025, 2, 13, 12, 0)
                        )
                )
        ));
    }
}
