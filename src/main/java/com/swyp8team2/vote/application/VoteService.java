package com.swyp8team2.vote.application;

import com.swyp8team2.common.exception.BadRequestException;
import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.post.domain.Post;
import com.swyp8team2.post.domain.PostImage;
import com.swyp8team2.post.domain.PostRepository;
import com.swyp8team2.post.domain.VoteType;
import com.swyp8team2.vote.presentation.dto.PostImageVoteStatusResponse;
import com.swyp8team2.user.domain.User;
import com.swyp8team2.user.domain.UserRepository;
import com.swyp8team2.vote.domain.Vote;
import com.swyp8team2.vote.domain.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final RatioCalculator ratioCalculator;

    @Transactional
    public Long vote(Long voterId, Long postId, Long imageId) {
        Optional<Vote> existsVote = voteRepository.findByUserIdAndPostImageId(voterId, imageId);
        if (existsVote.isPresent()) {
            return existsVote.get().getId();
        }
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));
        post.validateProgress();

        User voter = userRepository.findById(voterId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));

        VoteType voteType = post.getVoteType();
        if (VoteType.SINGLE.equals(voteType)) {
            deleteVoteIfExisting(post, voter.getId());
        }
        Vote vote = createVote(post, imageId, voter.getId());
        return vote.getId();
    }

    private void deleteVoteIfExisting(Post post, Long userId) {
        List<Vote> votes = voteRepository.findByUserIdAndPostId(userId, post.getId());
        for (Vote vote : votes) {
            voteRepository.delete(vote);
            post.cancelVote(vote.getPostImageId());
        }
    }

    private Vote createVote(Post post, Long imageId, Long userId) {
        Vote vote = voteRepository.save(Vote.of(post.getId(), imageId, userId));
        post.vote(imageId);
        return vote;
    }

    @Transactional
    public void cancelVote(Long userId, Long voteId) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.VOTE_NOT_FOUND));
        if (!vote.isVoter(userId)) {
            throw new BadRequestException(ErrorCode.NOT_VOTER);
        }
        voteRepository.delete(vote);
        Post post = postRepository.findById(vote.getPostId())
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));
        post.cancelVote(vote.getPostImageId());
    }

    public List<PostImageVoteStatusResponse> findVoteStatus(Long userId, Long postId) {
        Post post = postRepository.findByIdFetchPostImage(postId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));
        validateVoteStatus(userId, post);
        int totalVoteCount = getTotalVoteCount(post.getImages());
        return post.getImages().stream()
                .map(image -> {
                    String ratio = ratioCalculator.calculate(totalVoteCount, image.getVoteCount());
                    return new PostImageVoteStatusResponse(image.getId(), image.getName(), image.getVoteCount(), ratio);
                }).toList();
    }

    private void validateVoteStatus(Long userId, Post post) {
        List<Vote> votes = voteRepository.findByUserIdAndPostId(userId, post.getId());
        if (!(post.isAuthor(userId) || !votes.isEmpty())) {
            throw new BadRequestException(ErrorCode.ACCESS_DENIED_VOTE_STATUS);
        }
    }

    private int getTotalVoteCount(List<PostImage> images) {
        int totalVoteCount = 0;
        for (PostImage image : images) {
            totalVoteCount += image.getVoteCount();
        }
        return totalVoteCount;
    }
}
