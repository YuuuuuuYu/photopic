package com.swyp8team2.vote.application;

import com.swyp8team2.image.domain.ImageFile;
import com.swyp8team2.image.domain.ImageFileRepository;
import com.swyp8team2.post.domain.Post;
import com.swyp8team2.post.domain.PostRepository;
import com.swyp8team2.support.IntegrationTest;
import com.swyp8team2.user.domain.User;
import com.swyp8team2.user.domain.UserRepository;
import com.swyp8team2.vote.domain.Vote;
import com.swyp8team2.vote.domain.VoteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.swyp8team2.support.fixture.FixtureGenerator.createImageFile;
import static com.swyp8team2.support.fixture.FixtureGenerator.createPost;
import static com.swyp8team2.support.fixture.FixtureGenerator.createUser;
import static org.assertj.core.api.Assertions.assertThat;
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
    @DisplayName("투표하기")
    void vote() {
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
                () -> assertThat(vote.getUserSeq()).isEqualTo(user.getSeq()),
                () -> assertThat(vote.getPostId()).isEqualTo(post.getId()),
                () -> assertThat(vote.getPostImageId()).isEqualTo(post.getImages().get(0).getId()),
                () -> assertThat(findPost.getImages().get(0).getVoteCount()).isEqualTo(1)
        );
    }

    @Test
    @DisplayName("투표하기 - 다른 이미지로 투표 변경한 경우")
    void vote_change() {
        // given
        User user = userRepository.save(createUser(1));
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
                () -> assertThat(vote.getUserSeq()).isEqualTo(user.getSeq()),
                () -> assertThat(vote.getPostId()).isEqualTo(post.getId()),
                () -> assertThat(vote.getPostImageId()).isEqualTo(post.getImages().get(1).getId()),
                () -> assertThat(findPost.getImages().get(0).getVoteCount()).isEqualTo(0),
                () -> assertThat(findPost.getImages().get(1).getVoteCount()).isEqualTo(1)
        );
    }
}
