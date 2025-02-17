package com.swyp8team2.vote.presentation;

import com.swyp8team2.auth.domain.UserInfo;
import com.swyp8team2.common.presentation.CustomHeader;
import com.swyp8team2.vote.presentation.dto.ChangeVoteRequest;
import com.swyp8team2.vote.presentation.dto.GuestVoteRequest;
import com.swyp8team2.vote.presentation.dto.VoteRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
@RequestMapping("/votes")
public class VoteController {

    @PostMapping("")
    public ResponseEntity<Void> vote(
            @Valid @RequestBody VoteRequest request,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/guest")
    public ResponseEntity<Void> guestVote(
            @RequestHeader(CustomHeader.GUEST_ID) String guestId,
            @Valid @RequestBody VoteRequest request
    ) {
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{voteId}")
    public ResponseEntity<Void> changeVote(
            @PathVariable("voteId") Long voteId,
            @Valid @RequestBody ChangeVoteRequest request,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/guest/{voteId}")
    public ResponseEntity<Void> changeGuestVote(
            @PathVariable("voteId") Long voteId,
            @RequestHeader(CustomHeader.GUEST_ID) String guestId,
            @Valid @RequestBody ChangeVoteRequest request
    ) {
        return ResponseEntity.ok().build();
    }
}
