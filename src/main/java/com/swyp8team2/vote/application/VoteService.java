package com.swyp8team2.vote.application;

import com.swyp8team2.common.exception.BadRequestException;
import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.post.domain.Post;
import com.swyp8team2.post.domain.PostRepository;
import com.swyp8team2.user.domain.User;
import com.swyp8team2.user.domain.UserRepository;
import com.swyp8team2.vote.domain.Vote;
import com.swyp8team2.vote.domain.VoteRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public Long vote(Long voterId, Long postId, Long imageId) {
        User voter = userRepository.findById(voterId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        deleteVoteIfExisting(postId, voter.getSeq());
        Vote vote = createVote(postId, imageId, voter.getSeq());
        return vote.getId();
    }

    private void deleteVoteIfExisting(Long postId, String userSeq) {
        voteRepository.findByUserSeqAndPostId(userSeq, postId)
                        .ifPresent(vote -> {
                            voteRepository.delete(vote);
                            postRepository.findById(postId)
                                    .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND))
                                    .cancelVote(vote.getPostImageId());
                        });
    }

    private Vote createVote(Long postId, Long imageId, String userSeq) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));
        post.validateProgress();
        Vote vote = voteRepository.save(Vote.of(post.getId(), imageId, userSeq));
        post.vote(imageId);
        return vote;
    }

    public Long guestVote(String guestId, Long postId, Long imageId) {
        deleteVoteIfExisting(postId, guestId);
        Vote vote = createVote(postId, imageId, guestId);
        return vote.getId();
    }
}
