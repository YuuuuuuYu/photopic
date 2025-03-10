package com.swyp8team2.vote.presentation;

import com.swyp8team2.auth.domain.UserInfo;
import com.swyp8team2.vote.presentation.dto.PostImageVoteStatusResponse;
import com.swyp8team2.vote.application.VoteService;
import com.swyp8team2.vote.presentation.dto.VoteRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping("/posts/{postId}/votes")
    public ResponseEntity<Void> vote(
            @PathVariable("postId") Long postId,
            @Valid @RequestBody VoteRequest request,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        voteService.vote(userInfo.userId(), postId, request.imageId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/votes/{voteId}")
    public ResponseEntity<Void> cancelVote(
            @PathVariable("voteId") Long voteId,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        voteService.cancelVote(userInfo.userId(), voteId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/posts/{postId}/votes/status")
    public ResponseEntity<List<PostImageVoteStatusResponse>> findVoteStatus(
            @PathVariable("postId") Long postId,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        return ResponseEntity.ok(voteService.findVoteStatus(userInfo.userId(), postId));
    }
}
