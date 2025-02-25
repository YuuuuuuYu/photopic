package com.swyp8team2.vote.application;

import com.swyp8team2.common.exception.BadRequestException;
import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.post.domain.PostRepository;
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
    private final PostRepository postRepository;

    @Transactional
    public Long vote(Long userId, Long postId, Long imageId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        voteRepository.findByUserSeqAndPostId(user.getSeq(), postId)
                        .ifPresent(vote -> deleteExistingVote(postId, vote));
        Vote vote = createVote(postId, imageId, user);
        return vote.getId();
    }

    private Vote createVote(Long postId, Long imageId, User user) {
        Vote vote = voteRepository.save(Vote.of(postId, imageId, user.getSeq()));
        postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND))
                .vote(imageId);
        return vote;
    }

    private void deleteExistingVote(Long postId, Vote vote) {
        voteRepository.delete(vote);
        postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND))
                .cancelVote(vote.getPostImageId());
    }
}
