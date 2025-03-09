package com.swyp8team2.vote.application;

import com.swyp8team2.common.exception.BadRequestException;
import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.image.domain.ImageFile;
import com.swyp8team2.image.domain.ImageFileRepository;
import com.swyp8team2.post.domain.*;
import com.swyp8team2.support.IntegrationTest;
import com.swyp8team2.user.domain.User;
import com.swyp8team2.user.domain.UserRepository;
import com.swyp8team2.vote.domain.Vote;
import com.swyp8team2.vote.domain.VoteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.swyp8team2.support.fixture.FixtureGenerator.createImageFile;
import static com.swyp8team2.support.fixture.FixtureGenerator.createMultiplePost;
import static com.swyp8team2.support.fixture.FixtureGenerator.createPost;
import static com.swyp8team2.support.fixture.FixtureGenerator.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class VoteServiceTest extends IntegrationTest {

    @Autowired
    VoteService voteService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    VoteRepository voteRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    ImageFileRepository imageFileRepository;

    @Test
    @DisplayName("단일 투표하기")
    void singleVote() {
        // given
        User user = userRepository.save(createUser(1));
        ImageFile imageFile1 = imageFileRepository.save(createImageFile(1));
        ImageFile imageFile2 = imageFileRepository.save(createImageFile(2));
        Post post = postRepository.save(createPost(user.getId(), imageFile1, imageFile2, 1));

        // when
        Long voteId = voteService.vote(user.getId(), post.getId(), post.getImages().get(0).getId());

        // then
        Vote vote = voteRepository.findById(voteId).get();
        Post findPost = postRepository.findById(post.getId()).get();
        assertAll(
                () -> assertThat(vote.getUserId()).isEqualTo(user.getId()),
                () -> assertThat(vote.getPostId()).isEqualTo(post.getId()),
                () -> assertThat(vote.getPostImageId()).isEqualTo(post.getImages().get(0).getId()),
                () -> assertThat(findPost.getImages().get(0).getVoteCount()).isEqualTo(1)
        );
    }

    @Test
    @DisplayName("단일 투표하기 - 다른 이미지로 투표 변경한 경우")
    void singleVote_change() {
        // given
        User user = userRepository.save(createUser(2));
        ImageFile imageFile1 = imageFileRepository.save(createImageFile(1));
        ImageFile imageFile2 = imageFileRepository.save(createImageFile(2));
        Post post = postRepository.save(createPost(user.getId(), imageFile1, imageFile2, 1));
        voteService.vote(user.getId(), post.getId(), post.getImages().get(0).getId());

        // when
        Long voteId = voteService.vote(user.getId(), post.getId(), post.getImages().get(1).getId());

        // then
        Vote vote = voteRepository.findById(voteId).get();
        Post findPost = postRepository.findById(post.getId()).get();
        assertAll(
                () -> assertThat(vote.getUserId()).isEqualTo(user.getId()),
                () -> assertThat(vote.getPostId()).isEqualTo(post.getId()),
                () -> assertThat(vote.getPostImageId()).isEqualTo(post.getImages().get(1).getId()),
                () -> assertThat(findPost.getImages().get(0).getVoteCount()).isEqualTo(0),
                () -> assertThat(findPost.getImages().get(1).getVoteCount()).isEqualTo(1)
        );
    }

    @Test
    @DisplayName("복수 투표하기")
    void multipleVote() {
        // given
        User user = userRepository.save(createUser(1));
        ImageFile imageFile1 = imageFileRepository.save(createImageFile(1));
        ImageFile imageFile2 = imageFileRepository.save(createImageFile(2));
        Post post = postRepository.save(createMultiplePost(user.getId(), imageFile1, imageFile2, 1));

        // when
        Long voteId1 = voteService.vote(user.getId(), post.getId(), post.getImages().get(0).getId());
        Long voteId2 = voteService.vote(user.getId(), post.getId(), post.getImages().get(1).getId());

        // then
        Vote vote1 = voteRepository.findById(voteId1).get();
        Vote vote2 = voteRepository.findById(voteId2).get();
        Post findPost = postRepository.findById(post.getId()).get();
        assertAll(
                () -> assertThat(vote1.getUserId()).isEqualTo(user.getId()),
                () -> assertThat(vote1.getPostId()).isEqualTo(post.getId()),
                () -> assertThat(vote1.getPostImageId()).isEqualTo(post.getImages().get(0).getId()),

                () -> assertThat(vote2.getUserId()).isEqualTo(user.getId()),
                () -> assertThat(vote2.getPostId()).isEqualTo(post.getId()),
                () -> assertThat(vote2.getPostImageId()).isEqualTo(post.getImages().get(1).getId()),

                () -> assertThat(findPost.getImages().get(0).getVoteCount()).isEqualTo(1),
                () -> assertThat(findPost.getImages().get(1).getVoteCount()).isEqualTo(1)
        );
    }

    @Test
    @DisplayName("투표하기 - 투표 마감된 경우")
    void vote_alreadyClosed() {
        // given
        User user = userRepository.save(createUser(1));
        ImageFile imageFile1 = imageFileRepository.save(createImageFile(1));
        ImageFile imageFile2 = imageFileRepository.save(createImageFile(2));
        Post post = postRepository.save(new Post(
                null,
                user.getId(),
                "description",
                Status.CLOSED,
                Scope.PRIVATE,
                List.of(
                        PostImage.create("뽀또A", imageFile1.getId()),
                        PostImage.create("뽀또B", imageFile2.getId())
                ),
                "shareUrl",
                VoteType.SINGLE
        ));

        // when
        assertThatThrownBy(() -> voteService.vote(user.getId(), post.getId(), post.getImages().get(0).getId()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.POST_ALREADY_CLOSED.getMessage());
    }

    @Test
    @DisplayName("투표 취소")
    void cancelVote() {
        // given
        User user = userRepository.save(createUser(1));
        ImageFile imageFile1 = imageFileRepository.save(createImageFile(1));
        ImageFile imageFile2 = imageFileRepository.save(createImageFile(2));
        Post post = postRepository.save(createPost(user.getId(), imageFile1, imageFile2, 1));
        Long voteId = voteService.vote(user.getId(), post.getId(), post.getImages().get(0).getId());

        // when
        voteService.cancelVote(user.getId(), voteId);

        // then
        boolean res = voteRepository.findById(voteId).isEmpty();
        Post findPost = postRepository.findById(post.getId()).get();
        assertAll(
                () -> assertThat(res).isEqualTo(true),
                () -> assertThat(findPost.getImages().get(0).getVoteCount()).isEqualTo(0)
        );
    }

    @Test
    @DisplayName("투표 취소 - 투표자가 아닌 경우")
    void cancelVote_notVoter() {
        // given
        User user = userRepository.save(createUser(1));
        ImageFile imageFile1 = imageFileRepository.save(createImageFile(1));
        ImageFile imageFile2 = imageFileRepository.save(createImageFile(2));
        Post post = postRepository.save(createPost(user.getId(), imageFile1, imageFile2, 1));
        Long voteId = voteService.vote(user.getId(), post.getId(), post.getImages().get(0).getId());

        // when then
        assertThatThrownBy(() -> voteService.cancelVote(2L, voteId))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.NOT_VOTER.getMessage());
    }
}
