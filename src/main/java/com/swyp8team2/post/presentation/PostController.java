package com.swyp8team2.post.presentation;

import com.swyp8team2.auth.domain.UserInfo;
import com.swyp8team2.common.dto.CursorBasePaginatedResponse;
import com.swyp8team2.post.presentation.dto.AuthorDto;
import com.swyp8team2.post.presentation.dto.PostResponse;
import com.swyp8team2.post.presentation.dto.SimplePostResponse;
import com.swyp8team2.post.presentation.dto.VoteResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    @PostMapping(value ="", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createPost(
            @Valid @RequestPart("description") String description,
            @Valid @RequestPart("files") List<MultipartFile> files,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{shareUrl}")
    public ResponseEntity<PostResponse> findPost(@PathVariable("shareUrl") String shareUrl) {
        return ResponseEntity.ok(new PostResponse(
                1L,
                new AuthorDto(
                        1L,
                        "author",
                        "https://image.photopic.site/imagePath/profile-image"
                ),
                "description",
                List.of(
                        new VoteResponseDto(1L, "https://image.photopic.site/imagePath/1", 62.75, true),
                        new VoteResponseDto(2L, "https://image.photopic.site/imagePath/2", 37.25, false)
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
                                "https://image.photopic.site/imagePath/1",
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
                                "https://image.photopic.site/imagePath/1",
                                "https://photopic.site/shareurl",
                                LocalDateTime.of(2025, 2, 13, 12, 0)
                        )
                )
        ));
    }
}
