package com.swyp8team2.comment.presentation;

import com.swyp8team2.auth.domain.UserInfo;
import com.swyp8team2.comment.presentation.dto.AuthorDto;
import com.swyp8team2.comment.presentation.dto.CommentResponse;
import com.swyp8team2.comment.presentation.dto.CreateCommentRequest;
import com.swyp8team2.common.dto.CursorBasePaginatedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {

    @PostMapping("")
    public ResponseEntity<Void> createComment(
            @PathVariable("postId") Long postId,
            @Valid @RequestBody CreateCommentRequest request,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("")
    public ResponseEntity<CursorBasePaginatedResponse<CommentResponse>> findComments(
            @PathVariable("postId") Long postId,
            @RequestParam(value = "cursor", required = false) Long cursor,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        CursorBasePaginatedResponse<CommentResponse> response = new CursorBasePaginatedResponse<>(
                1L,
                false,
                List.of(
                        new CommentResponse(
                                1L,
                                "content",
                                new AuthorDto(1L, "author", "https://image.com/profile-image"),
                                1L,
                                LocalDateTime.of(2025, 2, 13, 12, 0)
                        )
                )
        );
        return ResponseEntity.ok(response);
    }
}
