package com.swyp8team2.vote.presentation;

import com.swyp8team2.auth.domain.UserInfo;
import com.swyp8team2.common.presentation.CustomHeader;
import com.swyp8team2.vote.application.VoteService;
import com.swyp8team2.vote.presentation.dto.ChangeVoteRequest;
import com.swyp8team2.vote.presentation.dto.VoteRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
