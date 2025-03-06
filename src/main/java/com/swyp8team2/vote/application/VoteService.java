package com.swyp8team2.vote.application;

import com.swyp8team2.common.exception.BadRequestException;
import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.post.domain.Post;
import com.swyp8team2.post.infrastructure.PostJpaRepository;
import com.swyp8team2.user.domain.User;
import com.swyp8team2.user.domain.UserRepository;
import com.swyp8team2.vote.domain.Vote;
import com.swyp8team2.vote.domain.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final PostJpaRepository postRepository;

    @Transactional
    public Long vote(Long voterId, Long postId, Long imageId) {
        User voter = userRepository.findById(voterId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        deleteVoteIfExisting(postId, voter.getId());
        Vote vote = createVote(postId, imageId, voter.getId());
        return vote.getId();
    }

    private void deleteVoteIfExisting(Long postId, Long userId) {
        voteRepository.findByUserIdAndPostId(userId, postId)
                        .ifPresent(vote -> {
                            voteRepository.delete(vote);
                            postRepository.findById(postId)
                                    .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND))
                                    .cancelVote(vote.getPostImageId());
                        });
    }

    private Vote createVote(Long postId, Long imageId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));
        post.validateProgress();
        Vote vote = voteRepository.save(Vote.of(post.getId(), imageId, userId));
        post.vote(imageId);
        return vote;
    }

    @Transactional
    public Long guestVote(Long userId, Long postId, Long imageId) {
        deleteVoteIfExisting(postId, userId);
        Vote vote = createVote(postId, imageId, userId);
        return vote.getId();
    }
}
