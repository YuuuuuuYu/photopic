package com.swyp8team2.post.application;

import com.swyp8team2.image.domain.ImageFile;
import com.swyp8team2.image.domain.ImageFileRepository;
import com.swyp8team2.post.domain.Post;
import com.swyp8team2.post.domain.PostRepository;
import com.swyp8team2.post.presentation.dto.PostResponse;
import com.swyp8team2.post.presentation.dto.PostImageResponse;
import com.swyp8team2.support.IntegrationTest;
import com.swyp8team2.user.domain.User;
import com.swyp8team2.user.domain.UserRepository;
import com.swyp8team2.vote.domain.Vote;
import com.swyp8team2.vote.domain.VoteRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static com.swyp8team2.support.fixture.FixtureGenerator.createImageFile;
import static com.swyp8team2.support.fixture.FixtureGenerator.createPost;
import static com.swyp8team2.support.fixture.FixtureGenerator.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PostQueryServiceTest extends IntegrationTest {

    @Autowired
    PostService postService;

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ImageFileRepository imageFileRepository;

    @Autowired
    VoteRepository voteRepository;

    @Test
    @DisplayName("게시글 조회")
    void findById() throws Exception {
        //given
        User user = userRepository.save(createUser(1));
        ImageFile imageFile1 = imageFileRepository.save(createImageFile(1));
        ImageFile imageFile2 = imageFileRepository.save(createImageFile(2));
        Post post = postRepository.save(createPost(user.getId(), imageFile1, imageFile2, 1));

        //when
        PostResponse response = postService.findById(user.getId(), post.getId());

        //then
        List<PostImageResponse> votes = response.images();
        assertAll(
                () -> assertThat(response.description()).isEqualTo(post.getDescription()),
                () -> assertThat(response.id()).isEqualTo(post.getId()),
                () -> assertThat(response.author().nickname()).isEqualTo(user.getNickname()),
                () -> assertThat(response.author().profileUrl()).isEqualTo(user.getProfileUrl()),
                () -> assertThat(response.shareUrl()).isEqualTo(post.getShareUrl()),
                () -> assertThat(votes).hasSize(2),
                () -> assertThat(votes.get(0).imageUrl()).isEqualTo(imageFile1.getImageUrl()),
                () -> assertThat(votes.get(0).voteId()).isNull(),
                () -> assertThat(votes.get(1).imageUrl()).isEqualTo(imageFile2.getImageUrl()),
                () -> assertThat(votes.get(1).voteId()).isNull()
        );
    }

    @Test
    @Disabled
    @DisplayName("유저가 작성한 게시글 조회 - 커서 null인 경우")
    void findUserPosts() throws Exception {
        //given
        User user = userRepository.save(createUser(1));
        List<Post> posts = createPosts(user);
        int size = 10;

        //when
        var response = postService.findUserPosts(user.getId(), null, size);

        //then
        assertAll(
                () -> assertThat(response.data()).hasSize(size),
                () -> assertThat(response.hasNext()).isTrue(),
                () -> assertThat(response.nextCursor()).isEqualTo(posts.get(posts.size() - size).getId())
        );
    }

    @Test
    @Disabled
    @DisplayName("유저가 작성한 게시글 조회 - 커서 있는 경우")
    void findUserPosts2() throws Exception {
        //given
        User user = userRepository.save(createUser(1));
        List<Post> posts = createPosts(user);
        int size = 10;

        //when
        var response = postService.findUserPosts(user.getId(), posts.get(3).getId(), size);

        //then
        assertAll(
                () -> assertThat(response.data()).hasSize(3),
                () -> assertThat(response.hasNext()).isFalse(),
                () -> assertThat(response.nextCursor()).isEqualTo(posts.get(0).getId())
        );
    }

    private List<Post> createPosts(User user) {
        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < 30; i += 2) {
            ImageFile imageFile1 = imageFileRepository.save(createImageFile(i));
            ImageFile imageFile2 = imageFileRepository.save(createImageFile(i + 1));
            posts.add(postRepository.save(createPost(user.getId(), imageFile1, imageFile2, i)));
        }
        return posts;
    }

    @Test
    @DisplayName("유저가 투표한 게시글 조회 - 커서 null인 경우")
    void findVotedPosts() throws Exception {
        //given
        User user = userRepository.save(createUser(1));
        List<Post> posts = createPosts(user);
        for (int i = 0; i < 15; i++) {
            Post post = posts.get(i);
            voteRepository.save(Vote.of(post.getId(), post.getImages().get(0).getId(), user.getId()));
        }
        int size = 10;

        //when
        var response = postService.findVotedPosts(user.getId(), null, size);

        //then
        int 전체_15개에서_맨_마지막_데이터_인덱스 = posts.size() - size;
        assertAll(
                () -> assertThat(response.data()).hasSize(size),
                () -> assertThat(response.hasNext()).isTrue(),
                () -> assertThat(response.nextCursor()).isEqualTo(posts.get(전체_15개에서_맨_마지막_데이터_인덱스).getId())
        );
    }
}
